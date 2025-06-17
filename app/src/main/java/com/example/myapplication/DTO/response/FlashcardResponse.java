package com.example.myapplication.DTO.response;


public class FlashcardResponse {
    private int id;
    private String word;
    private String phonetic;
    private String imageUrl;
    private String audioUrl;
    private String meaning;
    private String example;
    private String exampleMeaning;


    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getExample() {
        return example;
    }

    public String getExampleMeaning() {
        return exampleMeaning;
    }

    public FlashcardResponse() {}

}

