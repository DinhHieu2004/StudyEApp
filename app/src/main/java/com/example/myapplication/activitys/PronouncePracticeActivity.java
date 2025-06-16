package com.example.myapplication.activitys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.Sentence;

import java.util.List;

public class PronouncePracticeActivity extends AppCompatActivity {

    private TextView practiceText;
    private TextView phoneticText; // Optional nếu có dữ liệu
    private List<Sentence> sentenceList;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pronounce_practive);

        practiceText = findViewById(R.id.practiceText);
        phoneticText = findViewById(R.id.phoneticText);

        sentenceList = (List<Sentence>) getIntent().getSerializableExtra("sentences");

        if (sentenceList != null && !sentenceList.isEmpty()) {
            showSentence(currentIndex);
        }

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (currentIndex < sentenceList.size() - 1) {
                currentIndex++;
                showSentence(currentIndex);
            }
        });

        Button tryAgainButton = findViewById(R.id.tryAgainButton);
        tryAgainButton.setOnClickListener(v -> showSentence(currentIndex));
    }

    private void showSentence(int index) {
        Sentence s = sentenceList.get(index);
        practiceText.setText(s.getContent());
        phoneticText.setText(""); // hoặc xử lý nếu có transcript
    }
}
