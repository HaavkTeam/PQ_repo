package org.pqproject.backend.pojo;

public class UserAnswerData {
    private String questionId;
    private String description;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private int ANumber;
    private int BNumber;
    private int CNumber;
    private int DNumber;
    private int AnswerNumber;
    private String CorrectPercentage;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getANumber() {
        return ANumber;
    }

    public void setANumber(int ANumber) {
        this.ANumber = ANumber;
    }

    public int getBNumber() {
        return BNumber;
    }

    public void setBNumber(int BNumber) {
        this.BNumber = BNumber;
    }

    public int getCNumber() {
        return CNumber;
    }

    public void setCNumber(int CNumber) {
        this.CNumber = CNumber;
    }

    public int getDNumber() {
        return DNumber;
    }

    public void setDNumber(int DNumber) {
        this.DNumber = DNumber;
    }

    public int getAnswerNumber() {
        return AnswerNumber;
    }

    public void setAnswerNumber(int answerNumber) {
        AnswerNumber = answerNumber;
    }
    public String getCorrectPercentage() {
        return CorrectPercentage;
    }
    public void setCorrectPercentage(String correctPercentage) {
        CorrectPercentage = correctPercentage;
    }

    public UserAnswerData(String questionId,String description, String optionA, String optionB, String optionC, String optionD, String answer, int aNumber, int bNumber, int cNumber, int dNumber, int answerNumber, String correctPercentage) {
        this.questionId = questionId;
        this.description = description;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.answer = answer;
        ANumber = aNumber;
        BNumber = bNumber;
        CNumber = cNumber;
        DNumber = dNumber;
        AnswerNumber = answerNumber;
        CorrectPercentage = correctPercentage;
    }
}
