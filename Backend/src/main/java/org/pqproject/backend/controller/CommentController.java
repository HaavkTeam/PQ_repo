package org.pqproject.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Comment> getCommentsByQuestionId(@RequestParam String questionId) {
        return commentService.getCommentsByQuestionId(questionId); // Return the list of comments for the specified question
    }

}
