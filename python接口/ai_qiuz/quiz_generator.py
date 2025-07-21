# quiz_generator.py
import re
import requests
import uuid
import logging
from typing import List
from config import settings
from schemas import QuestionItem

logger = logging.getLogger(__name__)


def generate_quiz_with_deepseek(content: str, num_questions: int) -> List[QuestionItem]:
    """使用DeepSeek API生成选择题"""
    system_prompt = f"""
    你是一位专业的教育内容设计师，请根据提供的教学内容生成{num_questions}道高质量选择题。要求：
    1. 每道题必须基于提供的核心知识点
    2. 格式严格遵循：问题\nA.选项1\nB.选项2\nC.选项3\nD.选项4\n答案：[字母]\n解析：[解释]
    3. 选项要具有迷惑性，正确答案需明确
    4. 题目难度适中，覆盖关键概念
    """

    headers = {
        "Authorization": f"Bearer {settings.DEEPSEEK_API_KEY}",
        "Content-Type": "application/json"
    }

    payload = {
        "model": "deepseek-chat",
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": content[:settings.MAX_TEXT_LENGTH]}
        ],
        "temperature": 0.3,
        "max_tokens": 2000
    }

    try:
        logger.info("调用DeepSeek API生成题目...")
        response = requests.post(settings.DEEPSEEK_API_URL, json=payload, headers=headers)
        response.raise_for_status()
        logger.info("DeepSeek API调用成功")
        return parse_quiz_output(response.json()["choices"][0]["message"]["content"])
    except Exception as e:
        logger.error(f"DeepSeek API调用失败: {str(e)}")
        return []


def parse_quiz_output(raw_text: str) -> List[QuestionItem]:
    """解析模型输出的题目文本为结构化数据"""
    logger.info("解析题目输出...")
    questions = []

    # 改进的正则表达式
    pattern = r"问题[\d：]*\s*(.*?)\s*A[.:]?\s*(.*?)\s*B[.:]?\s*(.*?)\s*C[.:]?\s*(.*?)\s*D[.:]?\s*(.*?)\s*答案[:：\s]*([A-D])"

    matches = re.findall(pattern, raw_text, re.DOTALL)

    if not matches:
        logger.warning(f"无法解析题目，原始输出:\n{raw_text}")
        return questions

    for match in matches:
        try:
            description, optA, optB, optC, optD, answer = match

            # 清理文本中的多余空格
            clean = lambda s: re.sub(r'\s+', ' ', s).strip()
            description, optA, optB, optC, optD = map(clean,
                                                      (description, optA, optB, optC, optD))

            # 生成唯一问题ID
            question_id = f"Q_{uuid.uuid4().hex[:8]}"

            logger.info(f"解析到题目: {description[:30]}...")

            questions.append(QuestionItem(
                question_id=question_id,
                speech_id="",
                description=description,
                optionA=optA,
                optionB=optB,
                optionC=optC,
                optionD=optD,
                answer=answer.upper(),
                isUsed=0
            ))
        except Exception as e:
            logger.error(f"解析题目时出错: {str(e)}")
            continue

    logger.info(f"成功解析 {len(questions)} 道题目")
    return questions