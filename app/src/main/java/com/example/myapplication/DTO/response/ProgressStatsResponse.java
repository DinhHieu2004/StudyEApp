package com.example.myapplication.DTO.response;

import java.time.LocalDate;

public class ProgressStatsResponse {
    private String date;
    private long totalQuestions;
    private long correctAnswers;
    private double accuracy;

    public ProgressStatsResponse(String date, long totalQuestions, long correctAnswers, double accuracy) {
        this.date = date;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.accuracy = accuracy;
    }
    public ProgressStatsResponse(){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(long correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "ProgressStatsResponse{" +
                "date=" + date +
                ", totalQuestions=" + totalQuestions +
                ", correctAnswers=" + correctAnswers +
                ", accuracy=" + accuracy +
                '}';
    }
}
