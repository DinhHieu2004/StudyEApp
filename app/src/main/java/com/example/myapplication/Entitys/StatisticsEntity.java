package com.example.myapplication.Entitys;

@Entity(tableName = "statistics")
public class StatisticsEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long totalQuestions;
    public long correctAnswers;
    public double accuracyPercentage;

    @NonNull
    public String date; // để biết ngày thống kê
}
