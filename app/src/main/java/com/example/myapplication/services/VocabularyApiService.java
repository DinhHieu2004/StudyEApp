package com.example.myapplication.services;

import com.example.myapplication.DTO.response.VocabularyResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VocabularyApiService {
    @GET("api/vocabularies/lession/{id}")
    Call<List<VocabularyResponse>> getVocabulariesByLessionId(@Path("id") Long lessionId);
}

