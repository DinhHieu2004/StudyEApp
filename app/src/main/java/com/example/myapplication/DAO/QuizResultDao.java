package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Entitys.QuizResultEntity;

import java.util.List;

@Dao
public interface QuizResultDao {
    @Query("SELECT * FROM quiz_results WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    List<QuizResultEntity> getQuizResults(String startDate, String endDate);

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    List<QuizResultEntity> getAllQuizResults();

    @Insert
    long insertQuizResult(QuizResultEntity quizResult);

    @Insert
    void insertQuizResults(List<QuizResultEntity> quizResults);

    @Query("DELETE FROM quiz_results")
    void deleteAllQuizResults();

    @Query("DELETE FROM quiz_results WHERE timestamp < :timestamp")
    void deleteOldResults(String timestamp);
}
