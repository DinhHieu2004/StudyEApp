package com.example.myapplication.activitys;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Entitys.Flashcard;
import com.example.myapplication.R;
import com.example.myapplication.adapters.FlashcardAdapter;

import java.util.Arrays;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private FlashcardAdapter adapter;
    private Button btnNext;

    private List<Flashcard> flashcards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);

        String topic = getIntent().getStringExtra("topic");

        flashcards = getFlashcardsByTopic(topic);

        adapter = new FlashcardAdapter(flashcards);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.setPadding(80, 0, 80, 0);

        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float scale = Math.max(0.85f, 1 - Math.abs(position));
                float alpha = Math.max(0.5f, 1 - Math.abs(position));
                page.setScaleX(scale);
                page.setScaleY(scale);
                page.setAlpha(alpha);
            }
        });

        btnNext.setOnClickListener(v -> {
            int nextItem = (viewPager.getCurrentItem() + 1) % flashcards.size();
            viewPager.setCurrentItem(nextItem);
        });
    }

    private List<Flashcard> getFlashcardsByTopic(String topic) {
        switch (topic) {
            case "Animals":
                return Arrays.asList(
                        new Flashcard("Cat", "Con mèo"),
                        new Flashcard("Dog", "Con chó"),
                        new Flashcard("Elephant", "Con voi")
                );
            case "Food":
                return Arrays.asList(
                        new Flashcard("Apple", "Quả táo"),
                        new Flashcard("Bread", "Bánh mì"),
                        new Flashcard("Cheese", "Phô mai")
                );
            case "Travel":
                return Arrays.asList(
                        new Flashcard("Airport", "Sân bay"),
                        new Flashcard("Ticket", "Vé"),
                        new Flashcard("Hotel", "Khách sạn")
                );
            default:
                return Arrays.asList(
                        new Flashcard("Hello", "Xin chào"),
                        new Flashcard("Thank you", "Cảm ơn")
                );
        }
    }
}
