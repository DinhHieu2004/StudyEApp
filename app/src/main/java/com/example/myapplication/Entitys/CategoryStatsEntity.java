package com.example.myapplication.Entitys;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_stats",
        foreignKeys = @ForeignKey(
                entity = StatisticsEntity.class,
                parentColumns = "id",
                childColumns = "statisticsId",
                onDelete = ForeignKey.CASCADE
        ))
public class CategoryStatsEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long statisticsId;
    public String categoryName;
    public int totalQuestions;
    public int correctAnswers;
    public double accuracy;

    public CategoryStatsEntity() {}

    public CategoryStatsEntity(long statisticsId, String categoryName,
                               int totalQuestions, int correctAnswers, double accuracy) {
        this.statisticsId = statisticsId;
        this.categoryName = categoryName;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.accuracy = accuracy;
    }
}