package org.pqproject.backend.service;

import org.pqproject.backend.mapper.QuestionMapper;
import org.pqproject.backend.pojo.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return questionMapper.getUnusedQuestion(speechId);
    }
}
