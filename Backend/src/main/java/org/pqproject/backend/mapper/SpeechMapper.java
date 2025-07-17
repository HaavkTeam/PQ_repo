package org.pqproject.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.pqproject.backend.pojo.Speech;

import java.util.Date;
import java.util.List;

@Mapper
public interface SpeechMapper {
    @Select("SELECT * FROM speechtable WHERE speechId = #{speechId}")
    public Speech getSpeechById(String speechId);

    @Select("SELECT * FROM speechtable")
    public List<Speech> getAllSpeeches();

    @Insert("INSERT INTO speechtable VALUES (#{speechId}, #{title}, #{description}, #{organizer}, #{speaker}, #{startTime}, #{endTime}, #{status})")
    void addSpeech(Speech speech);

    //修改基本信息
    @Insert("UPDATE speechtable SET title = #{title}, description = #{description}, organizer = #{organizer}, speaker = #{speaker} WHERE speechId = #{speechId}")
    void updateSpeech(Speech speech);

    //删除演讲
    @Delete("DELETE FROM speechtable WHERE speechId = #{speechId}")
    void deleteSpeech(String speechId);

    //开始演讲
    @Update("UPDATE speechtable SET status = 1 ,startTime = #{startTime} WHERE speechId = #{speechId}")
    void startSpeech(String speechId, Date startTime);

    //结束演讲
    @Update("UPDATE speechtable SET status = 2 ,endTime = #{endTime} WHERE speechId = #{speechId}")
    void endSpeech(String speechId, Date endTime);

    //用户加入演讲
    @Insert("INSERT INTO surelation (speechId, userId) VALUES (#{speechId}, #{userId})")
    void joinSpeech(String speechId, String userId);

    //获取用户参与的演讲
    @Select("SELECT speechId FROM surelation WHERE userId = #{userId}")
    List<String> getMySpeeches(String userId);

    @Select("SELECT * FROM speechtable WHERE speaker = #{speaker}")
    List<Speech> getSpeechesBySpeaker(String speaker);

    @Select("SELECT * FROM speechtable WHERE organizer = #{organizer}")
    List<Speech> getSpeechesByOrganizer(String organizer);
}
