package com.example.myapplication.model;

public class UserProgress {
    private int partId;
    private int sentenceId;
    private String sentenceContent;
    private String userSpeech;
    private int score;
    private String status;
    private boolean isCompleted;
    private long timestamp;

    public UserProgress(int partId, int sentenceId, String sentenceContent, String userSpeech, int score, String status, boolean isCompleted, long timestamp) {
        this.partId = partId;
        this.sentenceId = sentenceId;
        this.sentenceContent = sentenceContent;
        this.userSpeech = userSpeech;
        this.score = score;
        this.status = status;
        this.isCompleted = isCompleted;
        this.timestamp = timestamp;
    }
    public UserProgress(){}

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public String getSentenceContent() {
        return sentenceContent;
    }

    public void setSentenceContent(String sentenceContent) {
        this.sentenceContent = sentenceContent;
    }

    public String getUserSpeech() {
        return userSpeech;
    }

    public void setUserSpeech(String userSpeech) {
        this.userSpeech = userSpeech;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}