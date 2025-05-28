package com.example.myapplication.DTO;

import java.io.Serializable;
import java.util.List;

public class AnswerDetail implements Serializable {
        private String questionText;
        private List<String> options;
        private String correctAnswer;
        private String userAnswer;


    public AnswerDetail(String questionText, List<String> options, String correctAnswer, String userAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.userAnswer = userAnswer;
    }
    public AnswerDetail(){}

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
