package org.pqproject.backend.pojo;

import java.util.Date;

public class ReturnSpeech {
    private String speechId;
    private String title;
    private String description;
    private String organizer;
    private String organizerName;
    private String speaker;
    private String speakerName;
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

    public int getStatus() {return status;}

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrganizerName() {
        return organizerName;
    }
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }
    public String getSpeakerName() {
        return speakerName;
    }
    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public void createReturnSpeech(Speech speech, String organizerName, String speakerName) {
        this.speechId = speech.getSpeechId();
        this.title = speech.getTitle();
        this.description = speech.getDescription();
        this.organizer = speech.getOrganizer();
        this.speaker = speech.getSpeaker();
        this.startTime = speech.getStartTime();
        this.endTime = speech.getEndTime();
        this.status = speech.getStatus();
        this.organizerName = organizerName;
        this.speakerName = speakerName;
    }
}
