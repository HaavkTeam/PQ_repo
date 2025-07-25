package org.pqproject.backend.pojo;

import java.util.List;

public class OrganizerData {
    private List<UserAnswerData> userAnswers;
    private int audienceCount;
    private String averageAccuracy; //average accuracy
    private int questionNumber; //question text

    public String getAverageAccuracy() {
        return averageAccuracy;
    }
    public void setAverageAccuracy(String averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    public int getAudienceCount() { return audienceCount; }
    public void setAudienceCount(int audienceCount) { this.audienceCount = audienceCount; }

    public List<UserAnswerData> getUserAnswers() {
        return userAnswers;
    }
    public void setUserAnswers(List<UserAnswerData> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }
    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }
}
