package com.example.myapplication.DTO.response;

import com.google.gson.annotations.SerializedName;

public class AuthenResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("uid")
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}