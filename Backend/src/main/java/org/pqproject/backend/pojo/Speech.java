package org.pqproject.backend.pojo;

import java.util.Date;

public class Speech {
    private String speechId;
    private String title;
    private String description;
    private String organizer;
    private String speaker;
    private Date startTime;
    private Date endTime;
    private int status;

    public String getSpeechId() {
        return speechId;
    }

    public void setSpeechId(String speechId) {
        this.speechId = speechId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void createDefaultSpeech() {
        this.speechId = "0";
        this.title = "Default Title";
        this.description = "Default Description";
        this.organizer = "Default Organizer";
        this.speaker = "Default Speaker";
        this.startTime = new Date();
        this.endTime = new Date();
        this.status = -1; // Default status
    }

    public String toString() {
        return "Speech{" +
                "speechId='" + speechId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", organizer='" + organizer + '\'' +
                ", speaker='" + speaker + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
}
