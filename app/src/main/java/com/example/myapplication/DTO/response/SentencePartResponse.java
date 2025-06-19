package com.example.myapplication.DTO.response;

import java.util.List;

public class SentencePartResponse {
    private List<String> parts;

    public SentencePartResponse(List<String> parts) {
        this.parts = parts;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }
}