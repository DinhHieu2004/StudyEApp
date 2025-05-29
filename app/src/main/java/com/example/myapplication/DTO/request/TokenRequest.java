package com.example.myapplication.DTO.request;

public class TokenRequest {
    private String idToken;

    public TokenRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }
}
