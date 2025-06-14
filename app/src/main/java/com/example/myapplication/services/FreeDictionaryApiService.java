package com.example.myapplication.services;

import com.example.myapplication.DTO.response.FreeDictionaryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FreeDictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    Call<List<FreeDictionaryResponse>> getWordDetails(@Path("word") String word);
}

