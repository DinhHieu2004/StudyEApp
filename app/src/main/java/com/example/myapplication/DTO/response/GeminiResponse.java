package com.example.myapplication.DTO.response;

import com.example.myapplication.DTO.GeminiCandidate;

import java.util.List;

public class GeminiResponse {
    private String answer;

    public GeminiResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
