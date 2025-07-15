package org.pqproject.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.pqproject.backend.pojo.Comment;
import java.util.List;

@Mapper
public interface CommentMapper {
    //添加评论
    @Insert("INSERT INTO commenttable VALUES (#{commentId}, #{questionId}, #{publisher}, #{replyId}, #{time})")
    void addComment(Comment comment);

    //删除评论
    @Insert("DELETE FROM commenttable WHERE commentId = #{commentId}")
    void deleteComment(String commentId);

    //获取不同题目的评论
     @Select("SELECT * FROM commenttable WHERE questionId = #{questionId}")
     List<Comment> getCommentsByQuestionId(String questionId);
}
