from pydantic import BaseModel
from typing import List, Optional

class ContentItem(BaseModel):
    file_id: str
    format: str
    content: str

class QuestionItem(BaseModel):
    question_id: str
    speech_id: str
    description: str
    optionA: str
    optionB: str
    optionC: str
    optionD: str
    answer: str
    isUsed: int

class QuizRequest(BaseModel):
    file: Optional[str] = None
    text: Optional[str] = None
    num_questions: int = 3

class QuizResponse(BaseModel):
    content_id: str
    questions: List[QuestionItem]

class ErrorResponse(BaseModel):
    detail: str