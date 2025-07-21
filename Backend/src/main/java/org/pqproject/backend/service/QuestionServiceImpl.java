package org.pqproject.backend.service;

import org.pqproject.backend.mapper.QuestionMapper;
import org.pqproject.backend.pojo.Question;
import org.pqproject.backend.pojo.ReturnSubmit;
import org.pqproject.backend.pojo.UserSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Question> launchQuestion(String speechId) {
        List<Question> questions =  questionMapper.getUnusedQuestion(speechId);
        if (questions.isEmpty()) {
            Question noQuestion = new Question();
            noQuestion.createDefault();
            questions.add(noQuestion); // Add a default question indicating no unused questions
            return questions; // No unused questions available
        }
        else {
            String testId = getUppercaseLetterAndNumber(8);
            questionMapper.insertTsRelation(testId, speechId);
            for (Question question : questions) {
                questionMapper.insertTqRelation(testId, question.getQuestionId());
                questionMapper.updateQuestionUsedStatus(question.getQuestionId());
            }
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
