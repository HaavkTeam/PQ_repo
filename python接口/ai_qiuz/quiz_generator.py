import re
import requests
import uuid
import logging
import time
import os
from typing import List
from config import settings
from schemas import QuestionItem

logger = logging.getLogger(__name__)


def generate_quiz_with_deepseek(content: str, num_questions: int) -> List[QuestionItem]:
    """使用DeepSeek API生成选择题"""
    # 简化系统提示，专注于生成指定数量的问题
    system_prompt = f"""
    你是一位专业的教育内容设计师，请根据提供的教学内容生成{num_questions}道高质量选择题。要求：

    1. 严格生成{num_questions}道题目，不要多也不要少
    2. 每道题格式：问题\nA.选项1\nB.选项2\nC.选项3\nD.选项4\n答案：[字母]\n解析：[解释]
    3. 选项设计要有合理迷惑性
    4. 题目要覆盖内容的关键知识点

    ## 示例格式
    问题：B+树的主要特点是什么？
    A. 所有节点都存储数据
    B. 非叶节点仅存储索引，数据全在叶子节点
    C. 支持随机访问但不支持范围查询
    D. 每个节点最多有2个子节点
    答案：B
    解析：B+树中非叶节点只存储键值作为索引，所有数据记录都存储在叶子节点中。
    """

    headers = {
        "Authorization": f"Bearer {settings.DEEPSEEK_API_KEY}",
        "Content-Type": "application/json"
    }

    # 优化payload，增加token限制
    payload = {
        "model": "deepseek-chat",
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": content[:settings.MAX_TEXT_LENGTH]}
        ],
        "temperature": 0.5,  # 稍高的温度以获得更多样化输出
        "max_tokens": 4000,  # 增加token限制以容纳更多题目
        "stop": None  # 禁用停止序列
    }

    try:
        # 确保API URL有效
        api_url = settings.DEEPSEEK_API_URL
        if not api_url.startswith("http"):
            logger.error(f"无效的API URL: {api_url}")
            return []

        logger.info(f"调用DeepSeek API生成题目，URL: {api_url}")
        logger.info(f"请求内容长度: {len(content)}")

        # 添加重试机制
        max_retries = 3
        retry_delay = 15  # 增加重试延迟
        response = None

        for attempt in range(max_retries):
            try:
                response = requests.post(
                    api_url,
                    json=payload,
                    headers=headers,
                    timeout=90  # 增加超时时间
                )
                # 如果成功，跳出循环
                break
            except requests.exceptions.Timeout:
                if attempt < max_retries - 1:
                    logger.warning(f"API请求超时，将在 {retry_delay} 秒后重试 (尝试 {attempt + 1}/{max_retries})")
                    time.sleep(retry_delay)
                    continue
                else:
                    logger.error(f"API请求超时，已达最大重试次数")
                    return []
            except requests.exceptions.RequestException as e:
                logger.error(f"API请求失败: {str(e)}")
                if attempt < max_retries - 1:
                    time.sleep(retry_delay)
                    continue
                else:
                    return []

        if response is None:
            logger.error("未获得有效API响应")
            return []

        logger.info(f"DeepSeek API响应状态: {response.status_code}")

        # 检查HTTP错误
        try:
            response.raise_for_status()
        except requests.exceptions.HTTPError as e:
            logger.error(f"API返回HTTP错误: {str(e)}")
            return []

        # 检查API响应结构
        try:
            response_data = response.json()
        except ValueError:
            logger.error("无法解析API响应为JSON")
            return []

        if "choices" not in response_data or not response_data["choices"]:
            logger.error("DeepSeek API返回无效响应结构")
            return []

        generated_text = response_data["choices"][0]["message"]["content"]
        logger.info(f"API返回内容长度: {len(generated_text)}")

        # 保存API响应到文件用于调试
        debug_dir = "api_debug_logs"
        if not os.path.exists(debug_dir):
            os.makedirs(debug_dir)
        debug_file = os.path.join(debug_dir, f"api_response_{int(time.time())}.txt")
        with open(debug_file, "w", encoding="utf-8") as f:
            f.write(generated_text)
        logger.info(f"API响应已保存到: {debug_file}")

        # 解析题目
        questions = parse_quiz_output(generated_text)

        # 如果题目数量不足，尝试更宽松的解析
        if len(questions) < num_questions:
            logger.warning(f"解析题目不足 ({len(questions)}/{num_questions})，尝试备用解析")
            questions = fallback_parse_quiz_output(generated_text)

        logger.info(f"最终解析题目数量: {len(questions)}")
        return questions

    except Exception as e:
        logger.exception(f"未知错误: {str(e)}")
        return []


def parse_quiz_output(raw_text: str) -> List[QuestionItem]:
    """解析模型输出的题目文本为结构化数据"""
    logger.info("解析题目输出...")
    questions = []

    # 改进分割方法：支持多种编号格式
    question_blocks = re.split(r'\n\s*(\d+[\.、\)])\s+', raw_text)

    # 第一个块通常是空字符串或说明文字，跳过
    if question_blocks and not question_blocks[0].strip():
        question_blocks = question_blocks[1:]

    # 重新组合块：编号+内容
    if question_blocks:
        blocks = []
        for i in range(0, len(question_blocks), 2):
            if i + 1 < len(question_blocks):
                blocks.append(question_blocks[i] + question_blocks[i + 1])
            else:
                blocks.append(question_blocks[i])
        question_blocks = blocks
    else:
        question_blocks = []

    if not question_blocks:
        logger.error("无法分割题目块")
        return questions

    logger.info(f"找到 {len(question_blocks)} 个题目块")

    for block in question_blocks:
        try:
            # 提取问题描述
            description_match = re.search(r'^(.*?)\nA[\.:]', block, re.DOTALL)
            if not description_match:
                # 尝试多种问题开头
                description_match = re.search(r'^(问题[:：]?\s*(.*?))\nA[\.:]', block, re.DOTALL)
                if description_match:
                    description = description_match.group(1).strip()
                else:
                    # 提取到第一个换行符
                    description = block.split('\n')[0].strip()
                    logger.warning(f"使用备用方法提取问题描述: {description[:50]}...")
            else:
                description = description_match.group(1).strip()

            # 提取选项 - 更灵活的模式
            options_pattern = r'[A-D][\.:、]\s*(.*?)(?=\n[A-D][\.:、]|\n答案|\n解析|\Z)'
            options = re.findall(options_pattern, block, re.DOTALL)

            if len(options) >= 4:
                optA, optB, optC, optD = options[:4]
                logger.debug(f"提取到选项: A={optA[:20]}... B={optB[:20]}...")
            else:
                # 备用方法：尝试标准模式
                options_match = re.search(r'A[\.:](.*?)\nB[\.:](.*?)\nC[\.:](.*?)\nD[\.:](.*?)\n', block, re.DOTALL)
                if options_match:
                    optA = options_match.group(1).strip()
                    optB = options_match.group(2).strip()
                    optC = options_match.group(3).strip()
                    optD = options_match.group(4).strip()
                else:
                    logger.warning(f"未找到选项: {block[:100]}...")
                    continue

            # 提取答案 - 增强灵活性
            answer_match = re.search(r'答案[:：]\s*\[?([A-D])\]?', block)
            if not answer_match:
                # 查找答案行
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
                    # 尝试在问题中找答案
                    answer_in_question = re.search(r'答案\s*是\s*([A-D])', description)
                    if answer_in_question:
                        answer = answer_in_question.group(1).upper()
                        logger.warning(f"从问题中提取答案: {answer}")
                    else:
                        logger.warning(f"未找到答案行: {block[:100]}...")
                        continue
            else:
                answer = answer_match.group(1).upper()

            # 生成唯一问题ID
            question_id = f"Q_{uuid.uuid4().hex[:8]}"

            logger.info(f"解析到题目: {description[:50]}...")

            # 创建问题对象
            question = QuestionItem(
                question_id=question_id,
                speech_id="",
                description=description,
                optionA=optA.strip(),
                optionB=optB.strip(),
                optionC=optC.strip(),
                optionD=optD.strip(),
                answer=answer,
                isUsed=0
            )

            questions.append(question)

        except Exception as e:
            logger.error(f"解析题目块时出错: {str(e)}")
            continue

    logger.info(f"成功解析 {len(questions)} 道题目")
    return questions


def fallback_parse_quiz_output(raw_text: str) -> List[QuestionItem]:
    """备用解析方法：更宽松的解析规则"""
    logger.info("使用备用解析方法...")
    questions = []

    # 尝试按问题分割
    question_blocks = re.split(r'\n\s*(问题\s*\d+[:：]?|问题[:：]|Q\d+[:：]|\d+[\.\)]\s+)', raw_text)

    # 重新组合块
    if question_blocks and not question_blocks[0].strip():
        question_blocks = question_blocks[1:]

    blocks = []
    for i in range(0, len(question_blocks), 2):
        if i + 1 < len(question_blocks):
            blocks.append(question_blocks[i] + question_blocks[i + 1])
        else:
            blocks.append(question_blocks[i])

    if not blocks:
        blocks = [raw_text]

    for block in blocks:
        try:
            # 提取问题描述 - 更宽松
            description = ""
            description_match = re.search(r'^(.*?)(?=A[\.:]|A选项|A\.|A：)', block, re.DOTALL)
            if description_match:
                description = description_match.group(1).strip()
            else:
                # 取前100字符作为问题
                description = block[:100].strip()

            # 提取选项 - 非常宽松
            options = []
            for letter in ['A', 'B', 'C', 'D']:
                option_match = re.search(f'{letter}[\.:：、]\s*(.*?)(?=\n|$)', block)
                if option_match:
                    options.append(option_match.group(1).strip())
                else:
                    # 如果没有找到，尝试下一个字母
                    continue

            if len(options) < 4:
                logger.warning(f"选项不足 ({len(options)}/4)，跳过该题")
                continue

            optA, optB, optC, optD = options[:4]

            # 提取答案 - 非常宽松
            answer = ""
            answer_match = re.search(r'答案\s*[:：]?\s*([A-D])', block)
            if answer_match:
                answer = answer_match.group(1).upper()
            else:
                # 尝试在解析中找答案
                explanation_match = re.search(r'解析\s*[:：]?\s*(.*?)(?=\n|$)', block)
                if explanation_match:
                    explanation = explanation_match.group(1)
                    answer_match = re.search(r'([A-D])', explanation)
                    if answer_match:
                        answer = answer_match.group(0).upper()

            if not answer:
                # 如果还是找不到答案，跳过该题
                logger.warning("无法提取答案，跳过该题")
                continue

            # 生成唯一问题ID
            question_id = f"Q_{uuid.uuid4().hex[:8]}"

            logger.info(f"备用方法解析到题目: {description[:50]}...")

            # 创建问题对象
            question = QuestionItem(
                question_id=question_id,
                speech_id="",
                description=description,
                optionA=optA,
                optionB=optB,
                optionC=optC,
                optionD=optD,
                answer=answer,
                isUsed=0
            )

            questions.append(question)

        except Exception as e:
            logger.error(f"备用解析出错: {str(e)}")
            continue

    logger.info(f"备用方法解析到 {len(questions)} 道题目")
    return questions