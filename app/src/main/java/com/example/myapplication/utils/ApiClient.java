package com.example.myapplication.utils;

import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/studyE/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();

                if (original.url().encodedPath().contains("/auth/login")) {
                    return chain.proceed(original);
                }

                String jwtToken = getJwtToken(context);
                Request.Builder requestBuilder = original.newBuilder();
                if (jwtToken != null) {
                    requestBuilder.addHeader("Authorization", "Bearer " + jwtToken);
                }
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
    }
    private static String getJwtToken(Context context) {
        if (context == null) return null;
        SharedPreferences prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return prefs.getString("jwt_token", null);
    }
}