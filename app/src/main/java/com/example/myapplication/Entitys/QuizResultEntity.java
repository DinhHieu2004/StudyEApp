package com.example.myapplication.Entitys;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_results")
public class QuizResultEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int score;
    private int total;
    private long duration;
    private String timestamp;

    public QuizResultEntity(){}

    public QuizResultEntity(int id, int score, int total, long duration, String timestamp){
        this.id = id;
        this.score = score;
        this.total = total;
        this.duration = duration;
        this.timestamp =timestamp;
    }

    public QuizResultEntity(int score, int total, long duration, String timestamp) {
        this.score = score;
        this.total = total;
        this.duration = duration;
        this.timestamp =timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "QuizResultEntity{" +
                "id=" + id +
                ", score=" + score +
                ", total=" + total +
                ", duration=" + duration +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
