package org.pqproject.backend.pojo;

//用户查看自己答题情况的返回类
public class ReturnSubmit {
    private String questionId;
    private String speechId;
    private String description;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String answer;
    private String selection;
    private int isCorrect;


    public String getQuestionId() {
        return questionId;
    }
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public String getSpeechId() {
        return speechId;
    }
    public void setSpeechId(String speechId) {
        this.speechId = speechId;
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
    public String getSelection() {
        return selection;
    }
    public void setSelection(String selection) {
        this.selection = selection;
    }
    public int getIsCorrect() {
        return isCorrect;
    }
    public void setIsCorrect(int isCorrect) {
        this.isCorrect = isCorrect;
    }

    public void createDefaultReturnSubmit(Question question, UserSubmit userSubmit) {
        this.questionId = question.getQuestionId();
        this.speechId = question.getSpeechId();
        this.description = question.getDescription();
        this.optionA = question.getOptionA();
        this.optionB = question.getOptionB();
        this.optionC = question.getOptionC();
        this.optionD = question.getOptionD();
        this.answer = question.getAnswer();
        this.selection = userSubmit.getSelection();
        this.isCorrect = userSubmit.getIsCorrect();
    }
}
