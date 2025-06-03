package com.example.myapplication.DTO.response;

import java.time.LocalDate;

public class ProgressStatsResponse {
    LocalDate date;
    long totalQuestions;
    long correctAnswers;
    double accuracy;
}
