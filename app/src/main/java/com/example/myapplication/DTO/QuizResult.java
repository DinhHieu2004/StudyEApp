package com.example.myapplication.DTO;

import java.io.Serializable;
import java.util.List;

public class QuizResult implements Serializable {
    private String userId;
    private int score;
    private int total;
    private long duration;
    private long timestamp;
    private List<AnswerDetail> answers;

    public QuizResult(String userId, int score, int total, long duration, long timestamp, List<AnswerDetail> answers) {
        this.userId = userId;
        this.score = score;
        this.total = total;
        this.duration = duration;
        this.timestamp = timestamp;
        this.answers = answers;
    }

    public QuizResult() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<AnswerDetail> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDetail> answers) {
        this.answers = answers;
    }
}

