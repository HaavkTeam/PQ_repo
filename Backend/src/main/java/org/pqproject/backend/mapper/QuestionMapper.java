package org.pqproject.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.pqproject.backend.pojo.Question;
import org.pqproject.backend.pojo.UserSubmit;
import org.pqproject.backend.pojo.test;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Select("SELECT * FROM questiontable WHERE questionId = #{questionId}")
    Question getQuestionsById(String questionId);

    @Select("SELECT * FROM questiontable WHERE speechId = #{speechId}")
    List<Question> getQuestionBySpeechId(String speechId);

    @Select("SELECT * FROM questiontable WHERE speechId = #{speechId} and isUsed = '0'")
    List<Question> getUnusedQuestion(String speechId);

    @Update("UPDATE questiontable SET isUsed = 1 WHERE questionId = #{questionId}")
    boolean updateQuestionUsedStatus(String questionId);

    @Select("SELECT answer FROM questiontable WHERE questionId = #{questionId}")
    String getAnswerById(String questionId);

    @Insert("INSERT INTO answerdata (questionId, userId, selection,isCorrect) VALUES (#{questionId}, #{userId}, #{selection},#{isCorrect})")
    boolean submitAnswer(UserSubmit userSubmit);

    @Select("SELECT * FROM answerdata WHERE questionId = #{questionId} AND userId = #{userId}")
    UserSubmit getUserAnswer(UserSubmit userSubmit);

    @Select("SELECT * FROM answerdata WHERE userId = #{userId} AND questionId = #{questionId}")
    UserSubmit getUserAnswerByUserIdAndQuestionId(String userId, String questionId);

    @Select("SELECT * FROM answerdata WHERE userId = #{userId}")
    List<UserSubmit> getUserSubmitsByUserId(String userId);

    @Select("SELECT * FROM answerdata WHERE questionId = #{questionId}")
    List<UserSubmit> getUserSubmitsByQuestionId(String questionId);

    @Insert("INSERT INTO tqrelation values (#{testId},#{questionId})")
    boolean insertTqRelation(String testId, String questionId);

    @Insert("INSERT INTO tsrelation values (#{testId},#{questionId},0)")
    boolean insertTsRelation(String testId, String questionId);

    @Select("SELECT * FROM tsrelation WHERE speechId = #{speechId}")
    List<test> getTestList(String speechId);

    @Select("SELECT questionId FROM tqrelation WHERE testId = #{testId}")
    List<String> getQuestionsByTestId(String testId);

    @Update("UPDATE tsrelation SET state = 1 WHERE testId = #{testId}")
    void changeTestStatus(String testId);
}
