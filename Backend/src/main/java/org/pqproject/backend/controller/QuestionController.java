package org.pqproject.backend.controller;

import org.pqproject.backend.pojo.Question;
import org.pqproject.backend.pojo.ReturnSubmit;
import org.pqproject.backend.pojo.UserSubmit;
import org.pqproject.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    //听众提交答案
    @RequestMapping("/submitAnswer")
    public String submitAnswer(@RequestBody UserSubmit submitAnswer) {
        if (questionService.submitAnswer(submitAnswer)) {
            return "答案提交成功"; // Return a success message
        } else {
            return "该题目已经作答，不可继续作答"; // Return an error message
        }
    }

    //听众查看自己的做题情况
    @RequestMapping("/getMySubmit")
    public List<ReturnSubmit> getMySubmit(@RequestParam("userId") String userId, @RequestParam("speechId") String speechId) {
        return questionService.getMySubmit(userId, speechId);
    }

    //演讲者上传自己的题目文件
    @RequestMapping("/uploadQuestionFile")
    public String uploadQuestionFile(@RequestParam("file") MultipartFile file, @RequestParam("speechId") String speechId) {
        // Implement the logic to handle file upload and question creation
        // This is a placeholder implementation
        return "文件上传成功，题目已创建"; // Return a success message
    }


}
