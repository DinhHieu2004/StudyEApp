package com.example.myapplication.DTO.response;

import java.util.List;

public class StatisticsResponse {
    long totalQuestions;
    long correctAnswers;
    double accuracyPercentage;
    List<CategoryStatsResponse> categoryStats;
    List<ProgressStatsResponse> progressStats;
}

