package org.pqproject.backend.pojo;

public class MyData {
    private String userId;
    private int AnswerNumber;
    private int ALLNumber;
    private String CorrectPercentage;

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
    public int getAnswerNumber() {
        return AnswerNumber;
    }
    public void setAnswerNumber(int answerNumber) {
        AnswerNumber = answerNumber;
    }
    public int getALLNumber() {
        return ALLNumber;
    }
    public void setALLNumber(int ALLNumber) {
        this.ALLNumber = ALLNumber;
    }
    public String getCorrectPercentage() {
        return CorrectPercentage;
    }
    public void setCorrectPercentage(String correctPercentage) {
        CorrectPercentage = correctPercentage;
    }
}
