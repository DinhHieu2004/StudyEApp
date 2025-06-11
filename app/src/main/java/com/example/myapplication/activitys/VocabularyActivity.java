package com.example.myapplication.activitys;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.response.VocabularyResponse;
import com.example.myapplication.R;
import com.example.myapplication.adapters.VocabularyAdapter;
import com.example.myapplication.services.VocabularyApiService;
import com.example.myapplication.utils.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VocabularyActivity extends AppCompatActivity {

    private RecyclerView recyclerVocabulary;
    private VocabularyAdapter adapter;
    private List<VocabularyResponse> vocabList = new ArrayList<>();

    private Long lessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);

        // Nhận lessionId từ intent
        lessionId = getIntent().getLongExtra("lessionId", -1);
        if (lessionId == -1) {
            Toast.makeText(this, "Không tìm thấy bài học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerVocabulary = findViewById(R.id.recyclerVocabulary);
        recyclerVocabulary.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VocabularyAdapter(this, vocabList);
        recyclerVocabulary.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });


        loadVocabulary();
    }

    private void loadVocabulary() {
        Retrofit retrofit = ApiClient.getClient(this);
        VocabularyApiService api = retrofit.create(VocabularyApiService.class);

        api.getVocabulariesByLessionId(lessionId).enqueue(new Callback<List<VocabularyResponse>>() {
            @Override
            public void onResponse(Call<List<VocabularyResponse>> call, Response<List<VocabularyResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vocabList.clear();
                    vocabList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(VocabularyActivity.this, "Lỗi khi tải từ vựng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<VocabularyResponse>> call, Throwable t) {
                Toast.makeText(VocabularyActivity.this, "Không kết nối được server", Toast.LENGTH_SHORT).show();
                Log.e("VocabularyAPI", "onFailure", t);
            }
        });

    }
}

