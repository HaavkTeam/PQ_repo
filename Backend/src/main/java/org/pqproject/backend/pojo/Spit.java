package org.pqproject.backend.pojo;

import java.util.Date;

public class Spit {
    private String SpitId;
    private String speechId;
    private String content;
    private Date time;

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String toString() {
        return "Spit [SpitId=" + SpitId + ", speechId=" + speechId + ", content=" + content + ", time=" + time + "]";
    }
}
