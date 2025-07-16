package org.pqproject.backend.controller;

import org.pqproject.backend.pojo.Question;
import org.pqproject.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    //获取一次演讲的所有问题
    @RequestMapping("/getQuestionsById")
    public List<Question> getQuestionsById(@RequestParam("speechId") String speechId) {
        return questionService.getQuestionsById(speechId);
    }

    //获取未使用的问题以便发布题目
    @RequestMapping("/launchQuestion")
    public List<Question> launchQuestion(@RequestParam("speechId") String speechId) {
        return questionService.launchQuestion(speechId);
    }

}
