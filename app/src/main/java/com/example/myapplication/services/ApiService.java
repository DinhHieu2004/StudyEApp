package com.example.myapplication.services;

import com.example.myapplication.DTO.request.QuestionFetchRequest;
import com.example.myapplication.DTO.response.OpenTriviaQuestionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("question")
    Call<List<OpenTriviaQuestionResponse>> fetchQuestions(@Body QuestionFetchRequest request);
}
