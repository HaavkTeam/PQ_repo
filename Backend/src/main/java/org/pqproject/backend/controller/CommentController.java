package org.pqproject.backend.controller;

import org.pqproject.backend.pojo.ReturnComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.pqproject.backend.service.CommentService;
import org.pqproject.backend.pojo.Comment;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @RequestMapping("/getCommentsByQuestionId")
    public List<ReturnComment> getCommentsByQuestionId(@RequestParam String questionId) {
        return commentService.getCommentsByQuestionId(questionId); // Return the list of comments for the specified question
    }

    @RequestMapping("/addComment")
    public boolean addComment(@RequestBody Comment comment) {
        return commentService.addComment(comment); // Add a new comment and return the result
    }

    @RequestMapping("/deleteComment")
    public boolean deleteComment(@RequestParam String commentId) {
        return commentService.deleteComment(commentId); // Delete a comment by its ID and return the result
    }

}
