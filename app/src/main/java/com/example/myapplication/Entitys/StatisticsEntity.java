package com.example.myapplication.Entitys;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "statistics")
public class StatisticsEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long totalQuestions;
    public long correctAnswers;
    public double accuracyPercentage;
    public long lastUpdated;

    public StatisticsEntity() {}

    public StatisticsEntity(long totalQuestions, long correctAnswers, double accuracyPercentage) {
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.accuracyPercentage = accuracyPercentage;
        this.lastUpdated = System.currentTimeMillis();
    }
}
