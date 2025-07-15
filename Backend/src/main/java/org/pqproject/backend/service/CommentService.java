package org.pqproject.backend.service;

import org.pqproject.backend.pojo.Comment;

import java.util.List;

public interface CommentService {
    //发表评论
    boolean addComment(Comment comment);

    //删除评论
    boolean deleteComment(String commentId);

    //获取评论
    List<Comment> getCommentsByQuestionId(String questionId);

    //排序评论，根据回复关系排序评论
    List<Comment> sortComments(List<Comment> comments);

}
