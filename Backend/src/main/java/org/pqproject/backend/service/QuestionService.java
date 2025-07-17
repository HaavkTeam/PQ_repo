package org.pqproject.backend.service;

import org.pqproject.backend.pojo.Question;
import org.pqproject.backend.pojo.ReturnSubmit;
import org.pqproject.backend.pojo.UserSubmit;

import java.util.List;

public interface QuestionService {

    /**
     * Retrieves a list of questions based on the specified type.
     *
     * @param speechId the type of questions to retrieve
     * @return a list of questions of the specified type
     */
    List<Question> getQuestionsById(String speechId);

    List<Question> launchQuestion(String speechId);

    boolean submitAnswer(UserSubmit submitAnswer);

    List<ReturnSubmit> getMySubmit(String userId, String speechId);
}
