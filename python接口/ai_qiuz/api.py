from fastapi import FastAPI, UploadFile, File, Form, HTTPException, Depends, Query
from typing import List, Optional
from processors import process_uploaded_file
from quiz_generator import generate_quiz_with_deepseek
from schemas import QuizRequest, QuizResponse, ContentItem, QuestionItem, ErrorResponse
from config import settings
from models import ContentTable, QuestionTable, get_db
from sqlalchemy.orm import Session
from sqlalchemy import text
import logging
import uuid
import os

logger = logging.getLogger(__name__)

app = FastAPI(
    title="AI-Pop-Quiz Generator API",
    version="1.0.0",
    description="API for generating quiz questions from educational content",
    responses={
        400: {"model": ErrorResponse},
        500: {"model": ErrorResponse}
    }
)


@app.get("/")
async def root():
    return {
        "message": "AI Pop Quiz Generator API is running!",
        "documentation": "http://localhost:8000/docs",
        "endpoints": {
            "generate_quiz": "POST /generate-quiz",
            "get_content": "GET /contents/{content_id}",
            "get_question": "GET /questions/{question_id}",
            "get_questions": "GET /questions?speech_id={speech_id}",
            "update_question_speech": "PUT /questions/{question_id}/speech",
            "mark_question_used": "PUT /questions/{question_id}/mark-used"
        }
    }


@app.get("/test-db")
async def test_db(db: Session = Depends(get_db)):
    try:
        # 使用 text() 包装 SQL 语句
        db.execute(text("SELECT 1"))
        return {"status": "success", "message": "Database connection OK"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Database error: {str(e)}")


@app.post(
    "/generate-quiz",
    response_model=QuizResponse,
    summary="生成AI选择题",
    description="根据文本内容或上传的文件生成选择题"
)
async def generate_quiz_endpoint(
        file: Optional[UploadFile] = File(None),
        text: Optional[str] = Form(None),
        speech_id: str = Form(..., description="演讲者ID"),
        num_questions: int = Form(settings.DEFAULT_NUM_QUESTIONS),
        db: Session = Depends(get_db)
):
    logger.info(
        f"收到生成题目请求: speech_id={speech_id}, "
        f"file={file.filename if file else None}, "
        f"text_length={len(text) if text else 0}, "
        f"num_questions={num_questions}")

    # 验证输入
    if not text and not file:
        raise HTTPException(status_code=400, detail="需要提供文本或文件")

    if num_questions < 1 or num_questions > 10:
        raise HTTPException(status_code=400, detail="题目数量必须在1到10之间")

    # 验证speech_id不为空
    if not speech_id.strip():
        raise HTTPException(
            status_code=400,
            detail="演讲者ID不能为空"
        )

    # 处理输入内容
    if text:
        logger.info("使用文本内容生成题目")
        raw_text = text
        file_format = "text"
    else:
        try:
            logger.info(f"处理上传文件: {file.filename}")
            file_content = await file.read()
            file_format = file.filename.split('.')[-1] if file.filename else ""
            raw_text = process_uploaded_file(file_content, file_format)
        except Exception as e:
            logger.error(f"处理输入时出错: {str(e)}")
            raise HTTPException(status_code=400, detail=f"文件处理失败: {str(e)}")

    # 验证提取的文本
    if not raw_text.strip():
        raise HTTPException(status_code=400, detail="无法从输入中提取有效文本")

    # 生成内容ID
    content_id = f"C_{uuid.uuid4().hex[:8]}"

    # 保存内容到数据库（关联speech_id）
    try:
        content_record = ContentTable(
            file_id=content_id,
            format=file_format,
            content=raw_text,
            speech_id=speech_id
        )
        db.add(content_record)
        db.commit()
        logger.info(f"内容已保存: content_id={content_id}, speech_id={speech_id}")
    except Exception as e:
        logger.error(f"保存内容记录失败: {str(e)}")
        raise HTTPException(status_code=500, detail=f"保存内容记录失败: {str(e)}")

    # 生成题目
    try:
        questions = generate_quiz_with_deepseek(raw_text, num_questions)
    except Exception as e:
        # 删除已保存的内容记录
        try:
            db.delete(content_record)
            db.commit()
        except:
            pass
        logger.error(f"题目生成失败: {str(e)}")
        raise HTTPException(status_code=500, detail=f"题目生成失败: {str(e)}")

    # 验证生成的题目
    if not questions:
        # 删除已保存的内容记录
        try:
            db.delete(content_record)
            db.commit()
        except:
            pass
        raise HTTPException(status_code=500, detail="未能生成有效的题目")

    # 保存题目到数据库（关联speech_id）
    question_records = []
    try:
        for question in questions:
            # 设置演讲者ID
            question.speech_id = speech_id

            db_question = QuestionTable(
                question_id=question.question_id,
                speech_id=speech_id,
                description=question.description,
                optionA=question.optionA,
                optionB=question.optionB,
                optionC=question.optionC,
                optionD=question.optionD,
                answer=question.answer,
                isUsed=0
            )
            db.add(db_question)
            question_records.append(question)

        db.commit()
        logger.info(f"成功保存 {len(question_records)} 道题目，speech_id={speech_id}")
    except Exception as e:
        # 回滚事务
        db.rollback()
        logger.error(f"保存题目记录失败: {str(e)}")
        # 尝试删除内容记录
        try:
            db.delete(content_record)
            db.commit()
        except:
            pass
        raise HTTPException(status_code=500, detail=f"保存题目记录失败: {str(e)}")

    return QuizResponse(
        content_id=content_id,
        questions=question_records
    )


# 获取内容记录
@app.get("/contents/{content_id}", response_model=ContentItem)
async def get_content(content_id: str, db: Session = Depends(get_db)):
    content = db.query(ContentTable).filter(ContentTable.file_id == content_id).first()
    if not content:
        raise HTTPException(status_code=404, detail="内容记录不存在")
    return ContentItem(
        file_id=content.file_id,
        format=content.format,
        content=content.content,
        speech_id=content.speech_id  # 添加speech_id
    )


# 获取单个问题记录
@app.get("/questions/{question_id}", response_model=QuestionItem)
async def get_question(question_id: str, db: Session = Depends(get_db)):
    question = db.query(QuestionTable).filter(QuestionTable.question_id == question_id).first()
    if not question:
        raise HTTPException(status_code=404, detail="问题记录不存在")
    return QuestionItem(
        question_id=question.question_id,
        speech_id=question.speech_id,
        description=question.description,
        optionA=question.optionA,
        optionB=question.optionB,
        optionC=question.optionC,
        optionD=question.optionD,
        answer=question.answer,
        isUsed=question.isUsed
    )


# 获取问题记录（支持按speech_id查询）
@app.get("/questions", response_model=List[QuestionItem])
async def get_questions(
        speech_id: Optional[str] = Query(None, description="演讲者ID"),
        db: Session = Depends(get_db)
):
    query = db.query(QuestionTable)

    if speech_id:
        # 验证speech_id不为空
        if not speech_id.strip():
            raise HTTPException(
                status_code=400,
                detail="演讲者ID不能为空"
            )
        query = query.filter(QuestionTable.speech_id == speech_id)

    questions = query.all()

    if not questions:
        raise HTTPException(status_code=404, detail="未找到问题记录")

    return [
        QuestionItem(
            question_id=q.question_id,
            speech_id=q.speech_id,
            description=q.description,
            optionA=q.optionA,
            optionB=q.optionB,
            optionC=q.optionC,
            optionD=q.optionD,
            answer=q.answer,
            isUsed=q.isUsed
        ) for q in questions
    ]


# 更新演讲ID
@app.put("/questions/{question_id}/speech")
async def update_question_speech(
        question_id: str,
        speech_id: str = Form(..., description="新的演讲者ID"),
        db: Session = Depends(get_db)):
    # 验证speech_id不为空
    if not speech_id.strip():
        raise HTTPException(
            status_code=400,
            detail="演讲者ID不能为空"
        )

    question = db.query(QuestionTable).filter(QuestionTable.question_id == question_id).first()
    if not question:
        raise HTTPException(status_code=404, detail="问题记录不存在")

    question.speech_id = speech_id
    db.commit()
    return {"status": "success", "message": f"问题 {question_id} 已关联到演讲 {speech_id}"}


# 标记问题为已使用
@app.put("/questions/{question_id}/mark-used")
async def mark_question_used(
        question_id: str,
        db: Session = Depends(get_db)):
    question = db.query(QuestionTable).filter(QuestionTable.question_id == question_id).first()
    if not question:
        raise HTTPException(status_code=404, detail="问题记录不存在")

    question.isUsed = 1
    db.commit()
    return {"status": "success", "message": f"问题 {question_id} 已标记为已使用"}


# 健康检查端点
@app.get("/health", include_in_schema=False)
async def health_check():
    return {"status": "healthy"}