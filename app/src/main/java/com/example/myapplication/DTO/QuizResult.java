package com.example.myapplication.DTO;

import android.speech.tts.TextToSpeech;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class QuizResult implements Serializable {
    private int score;
    private int total;
    private long duration;
    private String timestamp;
    private List<AnswerDetail> answers;

    public QuizResult(int score, int total, long duration, String timestamp, List<AnswerDetail> answers) {
        this.score = score;
        this.total = total;
        this.duration = duration;
        this.timestamp = timestamp;
        this.answers = answers;
    }

    public QuizResult() {

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<AnswerDetail> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDetail> answers) {
        this.answers = answers;
    }


}

