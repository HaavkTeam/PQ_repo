package org.pqproject.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.pqproject.backend.pojo.Question;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Select("SELECT * FROM questiontable WHERE speechId = #{speechId}")
    List<Question> getQuestionBySpeechId(String speechId);

    @Select("SELECT * FROM questiontable WHERE speechId = #{speechId} and isUsed = 0")
    List<Question> getUnusedQuestion(String speechId);
}
