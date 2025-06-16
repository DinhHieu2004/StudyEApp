package com.example.myapplication.DTO.response;

import com.example.myapplication.model.Sentence;

import java.util.List;

public class SentenceResponse {
    private List<Sentence> parts;
    private String message;
    private boolean success;

    public SentenceResponse() {}

    public List<Sentence> getParts() {
        return parts;
    }

    public void setParts(List<Sentence> parts) {
        this.parts = parts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}