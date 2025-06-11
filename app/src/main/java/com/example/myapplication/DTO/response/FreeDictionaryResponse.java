package com.example.myapplication.DTO.response;

import com.example.myapplication.DTO.Meaning;
import com.example.myapplication.DTO.Phonetic;

import java.util.List;

public class FreeDictionaryResponse {
    private String word;
    private List<Phonetic> phonetics;
    private List<Meaning> meanings;

    public String getWord() { return word; }
    public List<Phonetic> getPhonetics() { return phonetics; }
    public List<Meaning> getMeanings() { return meanings; }
}
