package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.response.SentencePartResponse;
import com.example.myapplication.R;
import com.example.myapplication.adapters.PartAdapter;
import com.example.myapplication.model.Sentence;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartListActivity extends AppCompatActivity {

    private RecyclerView partsRecyclerView;
    private PartAdapter partAdapter;
    private TextView levelTitle;
    private ImageView backButton;

    private String currentLevel;
    private List<String> partsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_list);

        initViews();
        getIntentData();
        setupRecyclerView();
        setupClickListeners();

        fetchPartsFromAPI();
    }

    private void initViews() {
        partsRecyclerView = findViewById(R.id.partsRecyclerView);
        levelTitle = findViewById(R.id.levelTitle);
        backButton = findViewById(R.id.backButton);
    }

    private void getIntentData() {
        currentLevel = getIntent().getStringExtra("level");
        partsList = getIntent().getStringArrayListExtra("parts_list");

        if (partsList == null) {
            partsList = new ArrayList<>();
        }

        // Cập nhật title theo level
        if (currentLevel != null) {
            levelTitle.setText("Level " + currentLevel + " - Parts");
        }
    }

    private void setupRecyclerView() {
        partAdapter = new PartAdapter(partsList, this::onPartClick);
        partsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partsRecyclerView.setAdapter(partAdapter);


    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchPartsFromAPI() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<SentencePartResponse> call = apiService.getPart(currentLevel);

        Log.d("PartListActivity", "Fetching parts for level: " + currentLevel);

        call.enqueue(new Callback<SentencePartResponse>() {
            @Override
            public void onResponse(Call<SentencePartResponse> call, Response<SentencePartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SentencePartResponse partResponse = response.body();

                    if (partResponse.getParts() != null && !partResponse.getParts().isEmpty()) {
                        Log.d("PartListActivity", "Successfully fetched " + partResponse.getParts().size() + " parts");

                        // Cập nhật danh sách và thông báo adapter
                        partsList.clear();
                        partsList.addAll(partResponse.getParts());
                        partAdapter.notifyDataSetChanged();



                    } else {
                        Toast.makeText(PartListActivity.this,
                                "Không tìm thấy phần nào cho cấp độ " + currentLevel,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("PartListActivity", "Error parsing error body: " + e.getMessage());
                    }
                    Log.e("PartListActivity", "Error fetching parts: " + response.code() + " - " + errorBody);
                    Toast.makeText(PartListActivity.this,
                            "Không thể tải danh sách phần (Error: " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SentencePartResponse> call, Throwable t) {
                Log.e("PartListActivity", "Network error: " + t.getMessage(), t);
                Toast.makeText(PartListActivity.this,
                        "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    private void onPartClick(String part) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<Sentence>> call = apiService.getSentence(currentLevel, part);


        call.enqueue(new Callback<List<Sentence>>() {
            @Override
            public void onResponse(Call<List<Sentence>> call, Response<List<Sentence>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Sentence> sentenceList = new ArrayList<>(response.body());

                    Intent intent = new Intent(PartListActivity.this, PronouncePracticeActivity.class);
                    intent.putExtra("sentences", sentenceList);
                    intent.putExtra("level", currentLevel);
                    intent.putExtra("part", part);
                    System.out.println("part intent "+ part);
                    startActivity(intent);
                } else {
                    Toast.makeText(PartListActivity.this,
                            "Không thể tải câu hỏi (Error: " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sentence>> call, Throwable t) {
                Toast.makeText(PartListActivity.this,
                        "Lỗi khi tải câu hỏi: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}