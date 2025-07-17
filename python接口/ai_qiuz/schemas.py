from pydantic import BaseModel
from typing import List, Optional

class QuizItem(BaseModel):
    question: str
    options: List[str]
    correct_index: int
    explanation: str

class QuizResponse(BaseModel):
    quizzes: List[QuizItem]
    original_text: str

class ErrorResponse(BaseModel):
    detail: str