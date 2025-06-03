package com.example.myapplication.Entitys;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "progress_stats")
public class ProgressStatsEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;
    public int correctAnswers;
    public int totalQuestions;

    public int statisticsId;
}
