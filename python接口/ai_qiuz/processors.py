# processors.py - 文件处理
import os
import pdfplumber
from pptx import Presentation
import speech_recognition as sr
import tempfile
from typing import Optional

def extract_text_from_pdf(file_path: str) -> str:
    """从PDF提取文本"""
    text = ""
    with pdfplumber.open(file_path) as pdf:
        for page in pdf.pages:
            page_text = page.extract_text()
            if page_text:
                text += page_text + "\n"
    return text.strip()

def extract_text_from_pptx(file_path: str) -> str:
    """从PPTX提取文本"""
    prs = Presentation(file_path)
    text = ""
    for slide in prs.slides:
        for shape in slide.shapes:
            if hasattr(shape, "text") and shape.text.strip():
                text += shape.text + "\n"
    return text.strip()

def extract_text_from_audio(file_path: str, language: str = "zh-CN") -> str:
    """从音频文件转文本"""
    r = sr.Recognizer()
    with sr.AudioFile(file_path) as source:
        audio_data = r.record(source)
        try:
            return r.recognize_google(audio_data, language=language)
        except sr.UnknownValueError:
            return ""
        except sr.RequestError as e:
            raise RuntimeError(f"语音识别服务错误: {e}")

def process_uploaded_file(file: bytes, file_type: str) -> str:
    """处理上传的文件并提取文本"""
    with tempfile.NamedTemporaryFile(delete=False, suffix=f".{file_type}") as tmp:
        tmp.write(file)
        tmp_path = tmp.name

    file_type = file_type.lower()

    # 处理文本文件
    if file_type in ["txt", "text"]:
        return file.decode("utf-8")

    try:
        file_type = file_type.lower()
        if file_type == "pdf":
            return extract_text_from_pdf(tmp_path)
        elif file_type in ["pptx", "ppt"]:
            return extract_text_from_pptx(tmp_path)
        elif file_type in ["mp3", "wav"]:
            return extract_text_from_audio(tmp_path)
        else:
            raise ValueError("不支持的文件格式")
    finally:
        os.unlink(tmp_path)


