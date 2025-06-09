package com.example.myapplication.DTO.request;

public class DictionaryRequest {
    private String word;

    public DictionaryRequest() {}

    public DictionaryRequest(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}