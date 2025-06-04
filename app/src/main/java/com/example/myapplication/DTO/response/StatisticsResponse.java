package com.example.myapplication.DTO.response;

import java.util.List;

public class StatisticsResponse {
    long totalQuestions;
    long correctAnswers;
    double accuracyPercentage;
    List<CategoryStatsResponse> categoryStats;
    List<ProgressStatsResponse> progressStats;

    public StatisticsResponse(long totalQuestions, long correctAnswers, double accuracyPercentage, List<CategoryStatsResponse> categoryStats, List<ProgressStatsResponse> progressStats) {
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.accuracyPercentage = accuracyPercentage;
        this.categoryStats = categoryStats;
        this.progressStats = progressStats;
    }
    public StatisticsResponse(){}

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

    public double getAccuracyPercentage() {
        return accuracyPercentage;
    }

    public void setAccuracyPercentage(double accuracyPercentage) {
        this.accuracyPercentage = accuracyPercentage;
    }

    public List<CategoryStatsResponse> getCategoryStats() {
        return categoryStats;
    }

    public void setCategoryStats(List<CategoryStatsResponse> categoryStats) {
        this.categoryStats = categoryStats;
    }

    public List<ProgressStatsResponse> getProgressStats() {
        return progressStats;
    }

    public void setProgressStats(List<ProgressStatsResponse> progressStats) {
        this.progressStats = progressStats;
    }

    @Override
    public String toString() {
        return "StatisticsResponse{" +
                "totalQuestions=" + totalQuestions +
                ", correctAnswers=" + correctAnswers +
                ", accuracyPercentage=" + accuracyPercentage +
                ", categoryStats=" + categoryStats +
                ", progressStats=" + progressStats +
                '}';
    }
}

