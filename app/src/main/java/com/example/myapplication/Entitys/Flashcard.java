package com.example.myapplication.Entitys;

public class Flashcard {
    private String word;
    private String meaning;

    public Flashcard(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }
}

