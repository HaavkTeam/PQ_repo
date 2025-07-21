from fastapi import FastAPI, UploadFile, File, Form, HTTPException, Depends
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
            "generate_quiz": "POST /generate-quiz"
        }
    }

# api.py
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
        num_questions: int = Form(settings.DEFAULT_NUM_QUESTIONS),
        db: Session = Depends(get_db)
):
    logger.info(
        f"收到生成题目请求: file={file.filename if file else None}, text_length={len(text) if text else 0}, num_questions={num_questions}")
    # 验证输入
    if not text and not file:
        raise HTTPException(status_code=400, detail="需要提供文本或文件")

    if num_questions < 1 or num_questions > 10:
        raise HTTPException(status_code=400, detail="题目数量必须在1到10之间")

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

    # 保存内容到数据库
    content_record = ContentTable(
        file_id=content_id,
        format=file_format,
        content=raw_text
    )
    db.add(content_record)
    db.commit()

    # 生成题目
    try:
        questions = generate_quiz_with_deepseek(raw_text, num_questions)
    except Exception as e:
        # 删除已保存的内容记录
        db.delete(content_record)
        db.commit()
        raise HTTPException(status_code=500, detail=f"题目生成失败: {str(e)}")

    # 验证生成的题目
    if not questions:
        # 删除已保存的内容记录
        db.delete(content_record)
        db.commit()
        raise HTTPException(status_code=500, detail="未能生成有效的题目")

    # 保存题目到数据库
    question_records = []
    for question in questions:
        db_question = QuestionTable(
            question_id=question.question_id,
            speech_id="",  # 演讲ID留空，后续填写
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
        content=content.content
    )


# 获取问题记录
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


# 更新演讲ID
@app.put("/questions/{question_id}/speech")
async def update_question_speech(
        question_id: str,
        speech_id: str = Form(...),
        db: Session = Depends(get_db)):
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