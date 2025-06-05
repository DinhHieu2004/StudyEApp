package com.example.myapplication.DTO.response;

public class CategoryStatsResponse {
    private String categoryName;
    private long totalQuestions;
    private long correctAnswers;
    private double accuracy;

    public CategoryStatsResponse(){}

    public CategoryStatsResponse(String categoryName, long totalQuestions, long correctAnswers, double accuracy) {
        this.categoryName = categoryName;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.accuracy = accuracy;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
        return "CategoryStatsResponse{" +
                "categoryName='" + categoryName + '\'' +
                ", totalQuestions=" + totalQuestions +
                ", correctAnswers=" + correctAnswers +
                ", accuracy=" + accuracy +
                '}';
    }
}
