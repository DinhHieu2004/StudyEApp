package com.example.myapplication.services;

import com.example.myapplication.DTO.response.DialogResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DialogApiService {
    @GET("api/dialogs/{lessionId}")
    Call<List<DialogResponse>> getDialogsByLessionId(@Path("lessionId") Long lessionId);
}

