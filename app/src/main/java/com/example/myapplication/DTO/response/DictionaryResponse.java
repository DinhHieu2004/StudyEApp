package com.example.myapplication.DTO.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DictionaryResponse {

    @SerializedName("word")
    private String word;

    @SerializedName("phonetics")
    private List<Phonetic> phonetics;

    @SerializedName("meanings")
    private List<Meaning> meanings;

    // Getter và Setter
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public List<Phonetic> getPhonetics() { return phonetics; }
    public void setPhonetics(List<Phonetic> phonetics) { this.phonetics = phonetics; }

    public List<Meaning> getMeanings() { return meanings; }
    public void setMeanings(List<Meaning> meanings) { this.meanings = meanings; }


    public static class Phonetic {
        @SerializedName("text")
        private String text;

        @SerializedName("audio")
        private String audio;

        // Getter và Setter
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getAudio() { return audio; }
        public void setAudio(String audio) { this.audio = audio; }
    }

    public static class Meaning {
        @SerializedName("partOfSpeech")
        private String partOfSpeech;

        @SerializedName("definitions")
        private List<Definition> definitions;

        // Getter và Setter
        public String getPartOfSpeech() { return partOfSpeech; }
        public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }

        public List<Definition> getDefinitions() { return definitions; }
        public void setDefinitions(List<Definition> definitions) { this.definitions = definitions; }
    }

    public static class Definition {
        @SerializedName("definition")
        private String definition;

        @SerializedName("vietnameseDefinition")
        private String vietnameseDefinition;

        @SerializedName("example")
        private String example;
        @SerializedName("vietnameseExample")
        private String vietnameseExample;

        // Getter và Setter
        public String getDefinition() { return definition; }
        public void setDefinition(String definition) { this.definition = definition; }

        public String getExample() { return example; }
        public void setExample(String example) { this.example = example; }

        public String getVietnameseDefinition() {
            return vietnameseDefinition;
        }

        public void setVietnameseDefinition(String vietnameseDefinition) {
            this.vietnameseDefinition = vietnameseDefinition;
        }

        public String getVietnameseExample() {
            return vietnameseExample;
        }

        public void setVietnameseExample(String vietnameseExample) {
            this.vietnameseExample = vietnameseExample;
        }
    }
}
