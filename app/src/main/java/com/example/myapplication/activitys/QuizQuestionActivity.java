package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.Question;
import com.example.myapplication.R;
import com.example.myapplication.adapters.QuestionAdapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class QuizQuestionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private long startTime;
    private QuestionAdapter adapter;
    private List<Question> questionList;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);

        startTime = System.currentTimeMillis();

        btnSubmit = findViewById(R.id.btn_submit);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách từ Intent
        questionList = (List<Question>) getIntent().getSerializableExtra("questions");

        adapter = new QuestionAdapter(questionList);
        recyclerView.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> submitQuiz());
    }

    private void submitQuiz() {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        HashMap<Integer, String> userAnswers = adapter.getUserAnswers();

        int score = 0;
        for (int i = 0; i < questionList.size(); i++) {
            String selected = userAnswers.get(i);
            if (selected != null && selected.equals(questionList.get(i).getCorrectAnswer())) {
                score++;
            }
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", questionList.size());
        intent.putExtra("questions", (Serializable) questionList);
        intent.putExtra("userAnswers", userAnswers);
        intent.putExtra("duration", duration);

        startActivity(intent);
    }


}
