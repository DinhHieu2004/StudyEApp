package com.example.myapplication.services;

import com.example.myapplication.DTO.response.LessionResponse;
import com.example.myapplication.DTO.response.PageResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LessionApiService {
    @GET("api/lessions")
    Call<PageResponse<LessionResponse>> getLessions(
            @Query("topicId") Long topicId,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("api/lessions/watched")
    Call<Void> markLessonWatched(@Query("lessonId") Long lessonId);

}

