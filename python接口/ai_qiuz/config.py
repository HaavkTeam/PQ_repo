import os

class Settings:
    DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY", "sk-05f1722d9a2c40ed9931c3368cf400b8")
    DEEPSEEK_API_URL = os.getenv("DEEPSEEK_API_URL", "https://api.deepseek.com/v1")
    MAX_TEXT_LENGTH = int(os.getenv("MAX_TEXT_LENGTH", "8000"))
    DEFAULT_NUM_QUESTIONS = int(os.getenv("DEFAULT_NUM_QUESTIONS", "1"))
    MODEL_NAME = "deepseek-reasoner"

settings = Settings()