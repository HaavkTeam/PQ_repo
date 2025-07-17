package org.pqproject.backend.service;

import org.pqproject.backend.mapper.CommentMapper;
import org.pqproject.backend.pojo.Comment;
import org.pqproject.backend.pojo.ReturnComment;
import org.pqproject.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.pqproject.backend.service.loginService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private loginService userService;

    // 实现添加评论的方法
    @Override
    public boolean addComment(Comment comment) {
        try {
            commentMapper.addComment(comment);
            return true; // 添加成功
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 添加失败
        }
    }

    // 实现删除评论的方法
    @Override
    public boolean deleteComment(String commentId) {
        try {
            commentMapper.deleteComment(commentId);
            return true; // 删除成功
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 删除失败
        }
    }

    // 实现获取评论的方法
    @Override
    public List<ReturnComment> getCommentsByQuestionId(String questionId) {
        try {
            List<Comment> comments = new ArrayList<>();
            List<ReturnComment> returnComments = new ArrayList<>();
            comments = commentMapper.getCommentsByQuestionId(questionId);
            for (Comment comment : comments) {
                // 设置评论的回复者昵称
                User user1 = userService.getUserById(comment.getPublisher());
                User user2 = userService.getUserById(commentMapper.getPublisherById(comment.getReplyId()));
                if (user1 != null) {
                    ReturnComment returnComment = new ReturnComment();
                    if( user2 != null) {
                        returnComment.createReturnComment(returnComment,comment,user1.getUsername(), user2.getUsername());
                        String str = returnComment.toString();
                        System.out.println("ReturnComment: " + str);
                    } else {
                        returnComment.createReturnComment(returnComment,comment, user1.getUsername(), "myself");
                    }
                    returnComments.add(returnComment);
                } else {

                }
            }
            return returnComments; // 返回指定题目的评论列表
            //return sortComments(commentMapper.getCommentsByQuestionId(questionId)); // 返回指定题目的评论列表
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 获取评论失败
        }
    }


}
