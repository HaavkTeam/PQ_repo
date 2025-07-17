import re
import requests
from typing import List, Dict
from config import settings
from schemas import QuizItem


def generate_quiz_with_deepseek(content: str, num_questions: int) -> List[QuizItem]:
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
        "model": "deepseek-llm-r1",
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": content[:settings.MAX_TEXT_LENGTH]}
        ],
        "temperature": 0.3,
        "max_tokens": 2000
    }

    response = requests.post(settings.DEEPSEEK_API_URL, json=payload, headers=headers)
    response.raise_for_status()

    return parse_quiz_output(response.json()["choices"][0]["message"]["content"])


def parse_quiz_output(raw_text: str) -> List[QuizItem]:
    """解析模型输出的题目文本为结构化数据"""
    quizzes = []
    # 改进的正则表达式，匹配更灵活的格式
    pattern = r"(问题\d*[:：]?\s*(.*?))\s*\n(A[.:]\s*(.*?)\s*\nB[.:]\s*(.*?)\s*\nC[.:]\s*(.*?)\s*\nD[.:]\s*(.*?)\s*\n答案[:：]\s*([A-D])\s*\n解析[:：]\s*(.*?)(?=\n\n|\n问题|$))"

    matches = re.findall(pattern, raw_text, re.DOTALL | re.IGNORECASE)

    for match in matches:
        try:
            _, question, _, opt_a, opt_b, opt_c, opt_d, answer, explanation = match

            # 清理选项文本
            options = [
                f"A. {opt_a.strip()}",
                f"B. {opt_b.strip()}",
                f"C. {opt_c.strip()}",
                f"D. {opt_d.strip()}"
            ]

            # 转换答案字母为索引
            correct_index = ord(answer.strip().upper()) - ord('A')

            quizzes.append(QuizItem(
                question=question.strip(),
                options=options,
                correct_index=correct_index,
                explanation=explanation.strip()
            ))
        except (IndexError, TypeError, ValueError) as e:
            # 跳过格式错误的题目
            continue

    return quizzes