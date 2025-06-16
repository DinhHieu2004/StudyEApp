package com.example.myapplication.Entitys;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "answer_details")
public class AnswerDetailEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int quizResultId; // Foreign key
    private String questionText;
    private String options; // JSON string
    private String correctAnswer;
    private String userAnswer;
    private String category;

    // Constructor, getters, setters
    public AnswerDetailEntity() {}

    public AnswerDetailEntity(int quizResultId, String questionText, String options,
                              String correctAnswer, String userAnswer, String category) {
        this.quizResultId = quizResultId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.userAnswer = userAnswer;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuizResultId() { return quizResultId; }
    public void setQuizResultId(int quizResultId) { this.quizResultId = quizResultId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
