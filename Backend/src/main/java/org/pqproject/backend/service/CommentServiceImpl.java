package org.pqproject.backend.service;

import org.pqproject.backend.mapper.CommentMapper;
import org.pqproject.backend.pojo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

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
    public List<Comment> getCommentsByQuestionId(String questionId) {
        try {
            return commentMapper.getCommentsByQuestionId(questionId);
            //return sortComments(commentMapper.getCommentsByQuestionId(questionId)); // 返回指定题目的评论列表
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 获取评论失败
        }
    }

    // 实现排序评论的方法
    @Override
    public List<Comment> sortComments(List<Comment> comments) {
        // 构建id到评论的映射
        Map<String, Comment> idMap = new HashMap<>();
        for (Comment c : comments) {
            idMap.put(c.getCommentId(), c);
        }

        // 构建父评论到子评论的映射
        Map<String, List<Comment>> childrenMap = new HashMap<>();
        for (Comment c : comments) {
            String parentId = c.getReplyId();
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(c);
        }

        List<Comment> result = new ArrayList<>();
        // 递归添加所有顶级评论及其子评论
        addChildren(null, childrenMap, result);
        return result;
    }

    private void addChildren(String parentId, Map<String, List<Comment>> childrenMap, List<Comment> result) {
        List<Comment> children = childrenMap.get(parentId);
        if (children == null) return;
        // 可按时间等排序
        // children.sort(Comparator.comparing(Comment::getCreateTime));
        for (Comment c : children) {
            result.add(c);
            addChildren(c.getCommentId(), childrenMap, result);
        }
    }

}
