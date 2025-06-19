package com.example.myapplication.Entitys;

public class Flashcard {
    private String word;
    private String phonetic;
    private String imageUrl;
    private String audioUrl;

    private String meaning;
    private String example;
    private String exampleMeaning;

    public Flashcard(String word, String phonetic, String imageUrl, String audioUrl,
                     String meaning, String example, String exampleMeaning) {
        this.word = word;
        this.phonetic = phonetic;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.meaning = meaning;
        this.example = example;
        this.exampleMeaning = exampleMeaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
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

    public String getExample() {
        return example;
    }

    public String getExampleMeaning() {
        return exampleMeaning;
    }
}

