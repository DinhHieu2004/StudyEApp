package com.example.myapplication.services;

import com.example.myapplication.DTO.QuizResult;
import com.example.myapplication.DTO.SubscriptionPlan;
import com.example.myapplication.DTO.request.GeminiRequest;
import com.example.myapplication.DTO.request.QuestionFetchRequest;

import com.example.myapplication.DTO.response.AuthenResponse;
import com.example.myapplication.DTO.response.DictionaryResponse;

import com.example.myapplication.DTO.response.GeminiResponse;
import com.example.myapplication.DTO.request.TokenRequest;
import com.example.myapplication.DTO.request.UserRequest;
import com.example.myapplication.DTO.response.LessionResponse;
import com.example.myapplication.DTO.response.OpenTriviaQuestionResponse;
import com.example.myapplication.DTO.response.PartProgressDTO;
import com.example.myapplication.DTO.response.PhonemeResponse;
import com.example.myapplication.DTO.response.SentencePartResponse;
import com.example.myapplication.DTO.response.StatisticsResponse;
//import com.example.myapplication.DTO.response.TopicResponse;
import com.example.myapplication.DTO.response.UserResponse;
import com.example.myapplication.DTO.response.VocabularyResponse;
import com.example.myapplication.model.PartProgress;
import com.example.myapplication.model.Sentence;
import com.example.myapplication.model.TextRequest;
import com.example.myapplication.model.UserProgress;
import com.example.myapplication.model.UserProgressForPronounce;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("question")
    Call<List<OpenTriviaQuestionResponse>> fetchQuestions(@Body QuestionFetchRequest request);
    @POST("auth/login")
    Call<AuthenResponse> loginWithFirebaseToken(@Body TokenRequest tokenRequest);
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
    @GET("dictionary/lookup")
    Call<DictionaryResponse> getWord(@Query("word") String word);

    @GET("api/lessions/watched")
    Call<List<LessionResponse>> getListLessonWatch();
    @GET("vocabulary/list")
    Call<List<VocabularyResponse>> getVocabularyByTopic(@Query("topicId") int topicId);
    @GET("auth/info")
    Call<UserResponse> getUserProfile(@Query("uid")String uid);
    @POST("auth/updateInfo")
    Call<Void> updateUserProfile(@Body UserResponse updated);

    @GET("api/subscriptions/plans")
    Call<List<SubscriptionPlan>> getSubscriptionPlans();

    @GET("sentence/part")
    Call<SentencePartResponse> getPart(@Query("level") String level);

    @GET("sentence")
   // Call<SentencePartResponse> getSentence(@Query("level") String level, @Query("part") String part );
    Call<List<Sentence>> getSentence(@Query("level") String level, @Query("part") String part);

    @POST("/api/phonemize")
    Call<PhonemeResponse> getPhonemes(@Body TextRequest request);

    // Lưu kết quả từng câu
    @POST("api/practice/save-result")
    Call<ResponseBody> savePracticeResult(@Body UserProgressForPronounce progress);

    // Lấy tiến độ của user cho part cụ thể
    @GET("api/practice/progress/{partId}")
    Call<PartProgressDTO> getPartProgress(@Path("partId") int partId, @Query("level") String level);

    // Lấy danh sách câu chưa hoàn thành
    @GET("api/practice/incomplete-sentences/{partId}")
    Call<List<Sentence>> getIncompleteSentences(@Path("partId") int partId, @Query("level") String level);

    // Cập nhật tiến độ part
    @POST("api/practice/update-progress")
    Call<PartProgressDTO> updatePartProgress(@Body Map<String, Object> progressData);

}
