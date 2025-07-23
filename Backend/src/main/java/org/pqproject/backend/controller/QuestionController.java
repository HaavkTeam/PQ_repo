package org.pqproject.backend.controller;

import org.pqproject.backend.pojo.*;
import org.pqproject.backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        return questionService.getUnusedQuestion(speechId);
    }

    //发布题目
    @RequestMapping("/publishQuestion")
    public String publishQuestion(@RequestBody List<String> questionList, @RequestParam("speechId") String speechId) {
        if (questionService.publishQuestion(questionList, speechId)) {
            return "题目发布成功"; // Return a success message
        } else {
            return "题目发布失败，可能是题目ID已存在"; // Return an error message
        }
    }

    //演讲者或听众获取测试列表
    @RequestMapping("/getTestList")
    public List<test> getTestList(@RequestParam("speechId") String speechId) {
        return questionService.getTestList(speechId);
    }

    //听众根据测试ID获取题目
    @RequestMapping("/getQuestionsByTestId")
    public List<Question> getQuestionsByTestId(@RequestParam("testId") String testId) {
        return questionService.getQuestionsByTestId(testId);
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

    //听众查看自己每一题的做题情况
    @RequestMapping("/getMySubmit")
    public List<ReturnSubmit> getMySubmit(@RequestParam("userId") String userId, @RequestParam("speechId") String speechId) {
        return questionService.getMySubmit(userId, speechId);
    }

    //听众获取总做题数据
    @RequestMapping("/getMyData")
    public MyData getMyData(@RequestParam("userId") String userId, @RequestParam("speechId") String speechId) {
        return questionService.getMyData(userId,speechId); // Return an empty MyData object for now
    }

    //演讲者上传自己的题目文件
    @PostMapping("/uploadQuestionFile")
    public String uploadQuestionFile(@RequestParam("file") MultipartFile file, @RequestParam("speechId") String speechId) throws IOException, InterruptedException {
        // Implement the logic to handle file upload and question creation
        // This is a placeholder implementation
        questionService.uploadQuestionFile(file, speechId);
        return "文件上传成功，题目已创建"; // Return a success message
    }

    //演讲者获取题目作答数据
    @RequestMapping("/getUserData")
    public List<UserAnswerData> getUserData(@RequestParam("testId") String testId) {
        return questionService.getUserData(testId); // Return the list of user answer data
    }




}
