package com.example.myapplication.DTO.request;

import com.example.myapplication.DTO.GeminiContent;

import java.util.List;

public class GeminiRequest {
    private String question;
    private String answer;

    public GeminiRequest(){}
    public GeminiRequest(String question, String answer){
        this.answer = answer;
        this.question =question;
    }

    @Override
    public String toString() {
        return "GeminiRequest{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
