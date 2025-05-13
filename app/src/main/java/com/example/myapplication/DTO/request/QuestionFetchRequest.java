package com.example.myapplication.DTO.request;

public class QuestionFetchRequest {
    private String amount;
    private String difficulty;
    private String category;

    public QuestionFetchRequest(String amount, String difficulty, String category) {
        this.amount = amount;
        this.difficulty = difficulty;
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "QuestionFetchRequest{" +
                "amount='" + amount + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                '}';
    }
}
