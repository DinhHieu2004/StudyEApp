package com.example.myapplication.Entitys;

@Entity(tableName = "category_stats")
public class CategoryStatsEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String category;
    public int correctAnswers;
    public int totalQuestions;

    public int statisticsId;
}
