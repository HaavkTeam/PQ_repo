package org.pqproject.backend.service;

import org.pqproject.backend.pojo.Comment;
import org.pqproject.backend.pojo.ReturnComment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    //发表评论
    boolean addComment(Comment comment);

    //删除评论
    boolean deleteComment(String commentId);

    //获取评论
    List<ReturnComment> getCommentsByQuestionId(String questionId);

}
