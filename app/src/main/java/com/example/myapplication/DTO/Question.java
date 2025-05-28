package com.example.myapplication.DTO;

import java.io.Serializable;

import org.apache.commons.text.StringEscapeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question implements Serializable {
    private String questionText;
    private String category;
    private String difficulty;
    private String correctAnswer;
    private List<String> options;

    public Question(String questionText, String category, String difficulty, List<String> options, String correctAnswer) {
        this.questionText = StringEscapeUtils.unescapeHtml4(questionText);
        this.category = category;
        this.difficulty = StringEscapeUtils.unescapeHtml4(difficulty);
        this.correctAnswer = StringEscapeUtils.unescapeHtml4(correctAnswer);
        this.options = new ArrayList<>();
        for (String option : options) {
            this.options.add(StringEscapeUtils.unescapeHtml4(option));
        }
        Collections.shuffle(this.options);
    }
    public Question(String questionText, List<String> options, String correctAnswer) {
        this.questionText = StringEscapeUtils.unescapeHtml4(questionText);
        this.category = category;
        this.difficulty = StringEscapeUtils.unescapeHtml4(difficulty);
        this.correctAnswer = StringEscapeUtils.unescapeHtml4(correctAnswer);
        this.options = new ArrayList<>();
        for (String option : options) {
            this.options.add(StringEscapeUtils.unescapeHtml4(option));
        }
        Collections.shuffle(this.options);
    }

    // Getters
    public String getQuestionText() { return questionText; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
    public String getCorrectAnswer() { return correctAnswer; }
    public List<String> getOptions() { return options; }

    @Override
    public String toString() {
        return "Question{" +
                "questionText='" + questionText + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", options=" + options +
                '}';
    }
}