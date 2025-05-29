package com.example.myapplication.services;

import com.example.myapplication.DTO.request.QuestionFetchRequest;
import com.example.myapplication.DTO.request.TokenRequest;
import com.example.myapplication.DTO.request.UserRequest;
import com.example.myapplication.DTO.response.OpenTriviaQuestionResponse;
import com.example.myapplication.DTO.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("question")
    Call<List<OpenTriviaQuestionResponse>> fetchQuestions(@Body QuestionFetchRequest request);
    @POST("auth/login")
    Call<UserResponse> loginWithFirebaseToken(@Body TokenRequest tokenRequest);
    @POST("auth/signUp")
    Call<Void> registerUser(@Body UserRequest userRequest);
}
