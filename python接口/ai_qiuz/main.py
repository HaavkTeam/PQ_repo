import uvicorn
from api import app

if __name__ == "__main__":
    uvicorn.run(
        "api:app",  # 修改为字符串格式的导入路径
        host="0.0.0.0",
        port=8000,
        log_level="info",
        reload=True
    )