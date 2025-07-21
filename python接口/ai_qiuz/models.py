from sqlalchemy import create_engine, Column, String, Integer
from sqlalchemy.orm import declarative_base, sessionmaker
from config import settings

Base = declarative_base()


class ContentTable(Base):
    __tablename__ = "filetable"
    file_id = Column('fileId', String(255), primary_key=True)  # 匹配数据库字段
    format = Column('format', String(255))
    content = Column('content', String(255))
    speech_id = Column('speechId', String(255))
class QuestionTable(Base):
    __tablename__ = "questiontable"
    question_id = Column('questionId', String(255), primary_key=True)  # 匹配数据库字段
    speech_id = Column('speechId', String(255))  # 匹配数据库字段
    description = Column('description', String(255))
    optionA = Column('optionA', String(255))
    optionB = Column('optionB', String(255))
    optionC = Column('optionC', String(255))
    optionD = Column('optionD', String(255))
    answer = Column('answer', String(255))
    isUsed = Column('isUsed', Integer)


# 初始化数据库引擎
engine = create_engine(settings.DATABASE_URL)
Base.metadata.create_all(bind=engine)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()