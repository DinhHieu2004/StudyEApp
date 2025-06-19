package com.example.myapplication.activitys;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.DTO.response.FlashcardResponse;
import com.example.myapplication.Entitys.Flashcard;
import com.example.myapplication.R;
import com.example.myapplication.adapters.FlashcardAdapter;
//import com.example.myapplication.fragments.SelectTopicFragment;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.services.VocabularyApiService;
import com.example.myapplication.utils.ApiClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlashcardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private FlashcardAdapter adapter;
    private Button btnNext;
    private ImageButton btnBack;
    private TextView tvCardCount;
    private List<Flashcard> flashcards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        viewPager = findViewById(R.id.viewPager);
        tvCardCount = findViewById(R.id.tvCardCount);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        long topicId = getIntent().getLongExtra("lessonId", -1);
        String topicName = getIntent().getStringExtra("lessionTitle");

        TextView tvTitle = findViewById(R.id.tvTitle);
        if (tvTitle != null && topicName != null) {
            tvTitle.setText(topicName);
        }

        if (topicId != -1) {
            VocabularyApiService apiService = ApiClient.getClient().create(VocabularyApiService.class);
            apiService.getVocabularyByTopic(topicId).enqueue(new Callback<List<FlashcardResponse>>() {
                @Override
                public void onResponse(Call<List<FlashcardResponse>> call, Response<List<FlashcardResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Flashcard> flashcards = convertToFlashcards(response.body());
                        setupViewPager(flashcards);
                    }
                }

                @Override
                public void onFailure(Call<List<FlashcardResponse>> call, Throwable t) {
                }
            });
        } else {
            setupViewPager(getFlashcardsByTopic("default"));
        }

        btnNext.setOnClickListener(v -> {
            int nextItem = (viewPager.getCurrentItem() + 1) % flashcards.size();
            viewPager.setCurrentItem(nextItem);
        });
        btnBack.setOnClickListener(v -> {
            finish();
        });

    }
    private void setupViewPager(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
        adapter = new FlashcardAdapter(flashcards);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.setPadding(80, 0, 80, 0);

        viewPager.setPageTransformer((page, position) -> {
            float scale = Math.max(0.85f, 1 - Math.abs(position));
            float alpha = Math.max(0.5f, 1 - Math.abs(position));
            page.setScaleX(scale);
            page.setScaleY(scale);
            page.setAlpha(alpha);
        });
        updateCardCount(0);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateCardCount(position);
            }
        });
    }

    private List<Flashcard> convertToFlashcards(List<FlashcardResponse> vocabList) {
        List<Flashcard> flashcards = new ArrayList<>();
        for (FlashcardResponse v : vocabList) {
            flashcards.add(new Flashcard(
                    v.getWord(),
                    v.getPhonetic(),
                    v.getImageUrl(),
                    v.getAudioUrl(),
                    v.getMeaning(),
                    v.getExample(),
                    v.getExampleMeaning()
            ));
        }
        return flashcards;
    }
    private List<Flashcard> getFlashcardsByTopic(String topic) {
        return Arrays.asList(
                new Flashcard("Hello", "/həˈləʊ/", "https://example.com/images/hello.jpg",
                        "https://example.com/audio/hello.mp3", "Xin chào",
                        "Hello, how are you?", "Xin chào, bạn khỏe không?")
        );
    }
    private void updateCardCount(int position) {
        String text = (position + 1) + " / " + (flashcards != null ? flashcards.size() : 0);
        tvCardCount.setText(text);
    }
}
