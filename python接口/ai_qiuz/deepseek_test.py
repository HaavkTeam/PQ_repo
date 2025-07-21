# test_deepseek.py
import requests
from config import settings

headers = {
    "Authorization": f"Bearer {settings.DEEPSEEK_API_KEY}",
    "Content-Type": "application/json"
}

payload = {
    "model": "deepseek-chat",
    "messages": [
        {"role": "system", "content": "你是一个有帮助的助手"},
        {"role": "user", "content": "你好"}
    ]
}

try:
    response = requests.post(
        settings.DEEPSEEK_API_URL,
        json=payload,
        headers=headers
    )
    response.raise_for_status()
    print("DeepSeek API 连接成功！")
    print(response.json())
except Exception as e:
    print(f"DeepSeek API 连接失败: {str(e)}")
    print(f"响应内容: {response.text if hasattr(e, 'response') else '无响应'}")