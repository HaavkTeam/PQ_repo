from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from typing import List, Optional
from processors import process_uploaded_file
from quiz_generator import generate_quiz_with_deepseek
from schemas import QuizResponse, ErrorResponse
from config import settings

app = FastAPI(
    title="AI-Pop-Quiz Generator API",
    version="1.0.0",
    description="API for generating quiz questions from educational content",
    responses={
        400: {"model": ErrorResponse},
        500: {"model": ErrorResponse}
    }
)


# 添加根路径路由
@app.get("/")
async def root():
    return {
        "message": "AI Pop Quiz Generator API is running!",
        "documentation": "http://localhost:8000/docs",
        "endpoints": {
            "generate_quiz": "POST /generate-quiz"
        }
    }


@app.post(
    "/generate-quiz",
    response_model=QuizResponse,
    summary="生成AI选择题",
    description="根据文本内容或上传的文件生成选择题"
)
async def generate_quiz_endpoint(
        file: Optional[UploadFile] = File(None),
        text: Optional[str] = Form(None),
        num_questions: int = Form(settings.DEFAULT_NUM_QUESTIONS)
):
    # 验证输入
    if not text and not file:
        raise HTTPException(status_code=400, detail="需要提供文本或文件")

    if num_questions < 1 or num_questions > 10:
        raise HTTPException(status_code=400, detail="题目数量必须在1到10之间")

    # 处理输入内容
    if text:
        raw_text = text
    else:
        try:
            file_content = await file.read()
            file_type = file.filename.split('.')[-1] if file.filename else ""
            raw_text = process_uploaded_file(file_content, file_type)
        except Exception as e:
            raise HTTPException(status_code=400, detail=f"文件处理失败: {str(e)}")

    # 验证提取的文本
    if not raw_text.strip():
        raise HTTPException(status_code=400, detail="无法从输入中提取有效文本")

    # 生成题目
    try:
        quizzes = generate_quiz_with_deepseek(raw_text, num_questions)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"题目生成失败: {str(e)}")

    # 验证生成的题目
    if not quizzes:
        raise HTTPException(status_code=500, detail="未能生成有效的题目")

    return QuizResponse(
        quizzes=quizzes,
        original_text=raw_text[:1000] + "..." if len(raw_text) > 1000 else raw_text
    )


# 健康检查端点
@app.get("/health", include_in_schema=False)
async def health_check():
    return {"status": "healthy"}