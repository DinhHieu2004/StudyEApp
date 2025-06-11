package com.example.myapplication.DTO.response;

import com.example.myapplication.DTO.UnsplashPhoto;

import java.util.List;

public class UnsplashResponse {
    private List<UnsplashPhoto> results;

    public List<UnsplashPhoto> getResults() {
        return results;
    }
}
