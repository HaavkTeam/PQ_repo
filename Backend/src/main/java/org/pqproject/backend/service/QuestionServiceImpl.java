package org.pqproject.backend.service;

import org.pqproject.backend.mapper.QuestionMapper;
import org.pqproject.backend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionMapper questionMapper;




    @Override
    public List<Question> getQuestionsById(String speechId) {
        return questionMapper.getQuestionBySpeechId(speechId);
    }

    @Override
    public List<Question> getUnusedQuestion(String speechId) {
        List<Question> questions =  questionMapper.getUnusedQuestion(speechId);
        if (questions.isEmpty()) {
            Question noQuestion = new Question();
            noQuestion.createDefault();
            questions.add(noQuestion); // Add a default question indicating no unused questions
            return questions; // No unused questions available
        }
        return questions;
    }

    @Override
    public boolean submitAnswer(UserSubmit submitAnswer) {
        UserSubmit existingAnswer = questionMapper.getUserAnswer(submitAnswer);
        if (existingAnswer != null) {
            return false; // User has already submitted an answer for this question
        }
        else {
            String answer = questionMapper.getAnswerById(submitAnswer.getQuestionId());
            if (submitAnswer.getSelection().equals(answer)) {
                submitAnswer.setIsCorrect(1); // Set isCorrect to true if the answer matches
            } else {
                submitAnswer.setIsCorrect(0); // Set isCorrect to false if the answer does not match
            }
            questionMapper.submitAnswer(submitAnswer);
            return true; // Answer submitted successfully
        }
    }

    @Override
    public List<ReturnSubmit> getMySubmit(String userId, String speechId) {
        List<ReturnSubmit> returnSubmits = new ArrayList<>();
        List<Question> questions = questionMapper.getQuestionBySpeechId(speechId);
        List<UserSubmit> userSubmits = questionMapper.getUserSubmitsByUserId(userId);
        for (Question question : questions) {
            boolean isAnswered = false;
            for (UserSubmit userSubmit : userSubmits) {
                if (question.getQuestionId().equals(userSubmit.getQuestionId())) {
                    ReturnSubmit returnSubmit = new ReturnSubmit();
                    returnSubmit.createDefaultReturnSubmit(question, userSubmit);
                    returnSubmits.add(returnSubmit);
                    isAnswered = true; // Mark as answered
                }
            }
            if (!isAnswered) {
                ReturnSubmit returnSubmit = new ReturnSubmit();
                UserSubmit defaultUserSubmit = new UserSubmit();
                defaultUserSubmit.createDefault();
                returnSubmit.createDefaultReturnSubmit(question, defaultUserSubmit); // No answer submitted
                returnSubmits.add(returnSubmit);
            }
        }
        return returnSubmits;
    }

    public void uploadQuestionFile(MultipartFile file, String speechId) throws IOException, InterruptedException {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String LINE_FEED = "\r\n";
        StringBuilder bodyBuilder = new StringBuilder();
        String numQuestions = "5";

        // speechId字段
        bodyBuilder.append("--").append(boundary).append(LINE_FEED);
        bodyBuilder.append("Content-Disposition: form-data; name=\"speech_id\"").append(LINE_FEED).append(LINE_FEED);
        bodyBuilder.append(speechId).append(LINE_FEED);

        // num_questions字段
        bodyBuilder.append("--").append(boundary).append(LINE_FEED);
        bodyBuilder.append("Content-Disposition: form-data; name=\"num_questions\"").append(LINE_FEED).append(LINE_FEED);
        bodyBuilder.append(numQuestions).append(LINE_FEED);

        // 文件字段
        bodyBuilder.append("--").append(boundary).append(LINE_FEED);
        bodyBuilder.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                .append(file.getOriginalFilename()).append("\"").append(LINE_FEED);
        bodyBuilder.append("Content-Type: ").append(file.getContentType()).append(LINE_FEED).append(LINE_FEED);

        byte[] fileBytes = file.getBytes();
        byte[] bodyStart = bodyBuilder.toString().getBytes();
        byte[] bodyEnd = (LINE_FEED + "--" + boundary + "--" + LINE_FEED).getBytes();

        byte[] requestBody = new byte[bodyStart.length + fileBytes.length + bodyEnd.length];
        System.arraycopy(bodyStart, 0, requestBody, 0, bodyStart.length);
        System.arraycopy(fileBytes, 0, requestBody, bodyStart.length, fileBytes.length);
        System.arraycopy(bodyEnd, 0, requestBody, bodyStart.length + fileBytes.length, bodyEnd.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/generate-quiz"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    public boolean publishQuestion(List<String> questionList, String speechId) {
        String testId = getUppercaseLetterAndNumber(8);
        for (String questionId : questionList) {
            questionMapper.updateQuestionUsedStatus(questionId); // Update the status of the question to used
            questionMapper.insertTqRelation(testId, questionId); // Insert the relation between test and question
        }
        questionMapper.insertTsRelation(testId, speechId);
        return true; // Return true to indicate that the questions were published successfully
    }

    public List<test> getTestList(String speechId){
        return questionMapper.getTestList(speechId);
    }

    public List<Question> getQuestionsByTestId(String testId){
        List<String> questionIds = questionMapper.getQuestionsByTestId(testId);
        List<Question> questions = new ArrayList<>();
        for (String questionId : questionIds) {
            Question question = questionMapper.getQuestionsById(questionId);
            if (question != null) {
                questions.add(question);
            }
            // Assuming you want to set other fields as well, you can fetch them from the database
            // or set them to default values.
        }
        return questions;
    }

    public MyData getMyData(String userId, String speechId){
        List<Question> questions = questionMapper.getQuestionBySpeechId(speechId);
        int questionCount = questions.size();
        int correctCount = 0;
        int answeredCount = 0;
        for (Question question : questions) {
            UserSubmit userSubmit = questionMapper.getUserAnswerByUserIdAndQuestionId(userId, question.getQuestionId());
            if (userSubmit == null) {
                continue;
            } else {
                answeredCount++;
                if (userSubmit.getIsCorrect() == 1) {
                    correctCount++;
                }
            }
        }
        MyData myData = new MyData();
        myData.setALLNumber(questionCount);
        myData.setUserId(userId);
        myData.setAnswerNumber(answeredCount);
        int correctPercentage = (correctCount * 100) / questionCount;
        myData.setCorrectPercentage(correctPercentage + "%");
        return myData;
    }

    public List<UserAnswerData> getUserData(String testId){
        List<UserAnswerData> userAnswerDataList = new ArrayList<>();
        List<String> questionIds = questionMapper.getQuestionsByTestId(testId);
        List<Question> questions = new ArrayList<>();
        for (String questionId : questionIds) {
            Question question = questionMapper.getQuestionsById(questionId);
            if (question != null) {
                questions.add(question);
            }
        }
        List<String> userIds = new ArrayList<>();

        // Collect unique user IDs
        for (Question question : questions) {
            List<UserSubmit> userSubmits = questionMapper.getUserSubmitsByQuestionId(question.getQuestionId());
            int aNumber = 0;
            int bNumber = 0;
            int cNumber = 0;
            int dNumber = 0;
            int AnswerNumber = 0;
            int correctNumber = 0;
            for (UserSubmit userSubmit : userSubmits) {
                AnswerNumber++;
                switch (userSubmit.getSelection()) {
                    case "A":
                        aNumber++;
                        break;
                    case "B":
                        bNumber++;
                        break;
                    case "C":
                        cNumber++;
                        break;
                    case "D":
                        dNumber++;
                        break;
                }
                if (userSubmit.getIsCorrect() == 1) {
                    correctNumber++;
                }
            }
            int correctPercentage = AnswerNumber==0 ? 0 : (correctNumber * 100) / AnswerNumber;
            UserAnswerData userAnswerData = new UserAnswerData(question.getQuestionId(), question.getDescription(),
                    question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD(),
                    question.getAnswer(), aNumber, bNumber, cNumber, dNumber, AnswerNumber, correctPercentage+ "%");
            userAnswerDataList.add(userAnswerData);
        }
        return userAnswerDataList;
    }

    public List<UserAnswerData> getUserData2(String speechId){
        List<UserAnswerData> userAnswerDataList = new ArrayList<>();
        List<Question> questions = questionMapper.getQuestionBySpeechId(speechId);

        // Collect unique user IDs
        for (Question question : questions) {
            List<UserSubmit> userSubmits = questionMapper.getUserSubmitsByQuestionId(question.getQuestionId());
            int aNumber = 0;
            int bNumber = 0;
            int cNumber = 0;
            int dNumber = 0;
            int AnswerNumber = 0;
            int correctNumber = 0;
            for (UserSubmit userSubmit : userSubmits) {
                AnswerNumber++;
                switch (userSubmit.getSelection()) {
                    case "A":
                        aNumber++;
                        break;
                    case "B":
                        bNumber++;
                        break;
                    case "C":
                        cNumber++;
                        break;
                    case "D":
                        dNumber++;
                        break;
                }
                if (userSubmit.getIsCorrect() == 1) {
                    correctNumber++;
                }
            }
            int correctPercentage = AnswerNumber==0 ? 0 : (correctNumber * 100) / AnswerNumber;
            UserAnswerData userAnswerData = new UserAnswerData(question.getQuestionId(), question.getDescription(),
                    question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD(),
                    question.getAnswer(), aNumber, bNumber, cNumber, dNumber, AnswerNumber, correctPercentage+ "%");
            userAnswerDataList.add(userAnswerData);
        }
        return userAnswerDataList;
    }


   public boolean changeTestStatus(String testId){
        questionMapper.changeTestStatus(testId);
        return true;
   }

    public  String getUppercaseLetterAndNumber(Integer length) {
        String uid = "";
        for (Integer i = 0; i < length; i++) {
            int index = (int) Math.round(Math.random() * 1);
            String randChar = "";
            switch (index) {
                case 0:
                    //大写字符
                    randChar = String.valueOf((char) Math.round(Math.random() * 25 + 97));
                    break;
                default:
                    //数字
                    randChar = String.valueOf(Math.round(Math.random() * 9));
                    break;
            }
            uid = uid.concat(randChar);
        }
        return uid;
    }
}
