package org.pqproject.backend.pojo;

public class Returnlogin {
    private User user;
    private int isOK;
    private String message;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public int getIsOK() {
        return isOK;
    }
    public void setIsOK(int isOK) {
        this.isOK = isOK;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
