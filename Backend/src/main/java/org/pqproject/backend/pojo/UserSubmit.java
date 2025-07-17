package org.pqproject.backend.pojo;

//前端传回的听众答题类
public class UserSubmit {
    private String questionId;
    private String userId;
    private String selection;
    private int isCorrect;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void createDefault() {
        this.questionId = "";
        this.userId = "";
        this.selection = "-1";
        this.isCorrect = -1 ;
    }
}
