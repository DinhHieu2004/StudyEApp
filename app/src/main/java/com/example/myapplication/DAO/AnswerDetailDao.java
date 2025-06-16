package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Entitys.AnswerDetailEntity;

import java.util.List;

@Dao
public interface AnswerDetailDao {
    @Query("SELECT * FROM answer_details WHERE quizResultId = :quizResultId")
    List<AnswerDetailEntity> getAnswerDetailsByQuizId(int quizResultId);

    @Insert
    void insertAnswerDetails(List<AnswerDetailEntity> answerDetails);

    @Query("DELETE FROM answer_details WHERE quizResultId = :quizResultId")
    void deleteAnswerDetailsByQuizId(int quizResultId);

    @Query("DELETE FROM answer_details")
    void deleteAllAnswerDetails();
}