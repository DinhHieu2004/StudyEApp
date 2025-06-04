package com.example.myapplication.Entitys;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "progress_stats",
        foreignKeys = @ForeignKey(
                entity = StatisticsEntity.class,
                parentColumns = "id",
                childColumns = "statisticsId",
                onDelete = ForeignKey.CASCADE
        ))
public class ProgressStatsEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long statisticsId;
    public String date;
    public double accuracy;

    public ProgressStatsEntity() {}

    public ProgressStatsEntity(long statisticsId, String date, double accuracy) {
        this.statisticsId = statisticsId;
        this.date = date;
        this.accuracy = accuracy;
    }
}