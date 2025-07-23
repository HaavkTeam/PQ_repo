package org.pqproject.backend.service;

import org.pqproject.backend.pojo.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {

    /**
     * Retrieves a list of questions based on the specified type.
     *
     * @param speechId the type of questions to retrieve
     * @return a list of questions of the specified type
     */
    List<Question> getQuestionsById(String speechId);

    List<Question> getUnusedQuestion(String speechId);

    boolean submitAnswer(UserSubmit submitAnswer);

    List<ReturnSubmit> getMySubmit(String userId, String speechId);


    void uploadQuestionFile(MultipartFile file, String speechId) throws IOException, InterruptedException;

    boolean publishQuestion(List<String> questionList, String speechId);

    List<test> getTestList(String speechId);

    List<Question> getQuestionsByTestId(String testId);

    MyData getMyData(String userId, String speechId);

    List<UserAnswerData> getUserData(String testId);

}
