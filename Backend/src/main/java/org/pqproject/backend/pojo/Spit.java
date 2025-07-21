package org.pqproject.backend.pojo;

public class Spit {
    private String SpitId;
    private String speechId;
    private String content;
    private String time;

    public String getSpitId() {
        return SpitId;
    }

    public void setSpitId(String spitId) {
        this.SpitId = spitId;
    }

    public String getSpeechId() {
        return speechId;
    }

    public void setSpeechId(String speechId) {
        this.speechId = speechId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
