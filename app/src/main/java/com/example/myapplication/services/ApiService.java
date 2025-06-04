package com.example.myapplication.services;

import com.example.myapplication.DTO.QuizResult;
import com.example.myapplication.DTO.request.GeminiRequest;
import com.example.myapplication.DTO.request.QuestionFetchRequest;
import com.example.myapplication.DTO.response.GeminiResponse;
import com.example.myapplication.DTO.request.TokenRequest;
import com.example.myapplication.DTO.request.UserRequest;
import com.example.myapplication.DTO.response.OpenTriviaQuestionResponse;
import com.example.myapplication.DTO.response.StatisticsResponse;
import com.example.myapplication.DTO.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("question")
    Call<List<OpenTriviaQuestionResponse>> fetchQuestions(@Body QuestionFetchRequest request);
    @POST("auth/login")
    Call<UserResponse> loginWithFirebaseToken(@Body TokenRequest tokenRequest);
    @POST("auth/signUp")
    Call<Void> registerUser(@Body UserRequest userRequest);

    @POST("quiz-results")
    Call<Void> saveQuizResult(@Body QuizResult result);

    @GET("statistics")
    Call<StatisticsResponse> statistics();

    @GET("question/history")
    Call<List<QuizResult>> getQuizHistory(@Query("start") String start,@Query("end") String end);

    @POST("v1beta/models/gemini-2.0-flash:generateContent")

    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,
            @Body GeminiRequest request
    );

    @POST("gemini")
    Call<GeminiResponse> getAnswerGemini(@Body GeminiRequest request);

}
