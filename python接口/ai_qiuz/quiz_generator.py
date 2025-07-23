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
    5. 每道题以数字编号开头（如"1. "）
    6. 答案格式请使用简单字母（如"A"）或带方括号的字母（如"[A]"）
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
        # 确保API URL有效
        api_url = settings.DEEPSEEK_API_URL
        if not api_url.startswith("http"):
            logger.error(f"无效的API URL: {api_url}")
            return []

        logger.info(f"调用DeepSeek API生成题目，URL: {api_url}")
        logger.info(f"请求内容长度: {len(content)}")

        response = requests.post(
            api_url,
            json=payload,
            headers=headers,
            timeout=30  # 设置30秒超时
        )

        # 记录API响应状态
        logger.info(f"DeepSeek API响应状态: {response.status_code}")

        # 检查HTTP错误
        response.raise_for_status()

        # 检查API响应结构
        response_data = response.json()
        if "choices" not in response_data or not response_data["choices"]:
            logger.error("DeepSeek API返回无效响应结构")
            return []

        generated_text = response_data["choices"][0]["message"]["content"]
        logger.info(f"API返回内容:\n{generated_text}")

        return parse_quiz_output(generated_text)

    except requests.exceptions.RequestException as e:
        logger.error(f"API请求失败: {str(e)}")
        return []
    except KeyError as e:
        logger.error(f"API响应缺少必要字段: {str(e)}")
        return []
    except ValueError as e:
        logger.error(f"JSON解析失败: {str(e)}")
        return []
    except Exception as e:
        logger.exception(f"未知错误: {str(e)}")
        return []


def parse_quiz_output(raw_text: str) -> List[QuestionItem]:
    """解析模型输出的题目文本为结构化数据"""
    logger.info("解析题目输出...")
    questions = []

    # 更灵活的分割方法：按题目编号分割
    question_blocks = re.split(r'\n\s*\d+\.\s+', raw_text)

    # 第一个块通常是空字符串或说明文字，跳过
    if question_blocks and not question_blocks[0].strip():
        question_blocks = question_blocks[1:]

    if not question_blocks:
        logger.error("无法分割题目块")
        return questions

    logger.info(f"找到 {len(question_blocks)} 个题目块")

    for block in question_blocks:
        try:
            # 提取问题描述
            description_match = re.search(r'^(.*?)\nA[\.:]', block, re.DOTALL)
            if not description_match:
                # 备用方法：提取到第一个换行符
                description = block.split('\n')[0].strip()
                logger.warning(f"使用备用方法提取问题描述: {description[:50]}...")
            else:
                description = description_match.group(1).strip()

            # 提取选项
            options_match = re.search(r'A[\.:](.*?)\nB[\.:](.*?)\nC[\.:](.*?)\nD[\.:](.*?)\n', block, re.DOTALL)
            if not options_match:
                # 备用方法：使用更灵活的模式
                options_pattern = r'[A-D][\.:]([^\n]*)'
                options = re.findall(options_pattern, block)
                if len(options) >= 4:
                    optA, optB, optC, optD = options[:4]
                    logger.warning(f"使用备用方法提取选项")
                else:
                    logger.warning(f"未找到选项: {block[:100]}...")
                    continue
            else:
                optA = options_match.group(1).strip()
                optB = options_match.group(2).strip()
                optC = options_match.group(3).strip()
                optD = options_match.group(4).strip()

            # 提取答案 - 增强灵活性
            answer_match = re.search(r'答案[:：]\s*\[?([A-D])\]?', block)
            if not answer_match:
                # 备用方法：查找答案行
                answer_line = re.search(r'答案[:：].*', block)
                if answer_line:
                    # 从答案行提取字母
                    answer_letter = re.search(r'[A-D]', answer_line.group(0))
                    if answer_letter:
                        answer = answer_letter.group(0)
                        logger.warning(f"使用备用方法提取答案: {answer}")
                    else:
                        logger.warning(f"未找到答案: {block[:100]}...")
                        continue
                else:
                    logger.warning(f"未找到答案行: {block[:100]}...")
                    continue
            else:
                answer = answer_match.group(1).upper()

            # 生成唯一问题ID
            question_id = f"Q_{uuid.uuid4().hex[:8]}"

            logger.info(f"解析到题目: {description[:50]}...")

            questions.append(QuestionItem(
                question_id=question_id,
                speech_id="",
                description=description,
                optionA=optA,
                optionB=optB,
                optionC=optC,
                optionD=optD,
                answer=answer,
                isUsed=0
            ))
        except Exception as e:
            logger.error(f"解析题目块时出错: {str(e)}")
            continue

    logger.info(f"成功解析 {len(questions)} 道题目")
    return questions
