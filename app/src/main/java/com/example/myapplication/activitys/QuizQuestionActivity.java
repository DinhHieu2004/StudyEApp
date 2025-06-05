package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.AnswerDetail;
import com.example.myapplication.DTO.Question;
import com.example.myapplication.DTO.QuizResult;
import com.example.myapplication.R;
import com.example.myapplication.adapters.QuestionAdapter;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        List<AnswerDetail> answerDetails = new ArrayList<>();
        for (int i = 0; i < questionList.size(); i++) {
            Question q = questionList.get(i);
            String userAnswer = userAnswers.get(i);

            AnswerDetail detail = new AnswerDetail();
            detail.setQuestionText(q.getQuestionText());
            detail.setOptions(q.getOptions());
            detail.setCorrectAnswer(q.getCorrectAnswer());
            detail.setCategory(q.getCategory());
            detail.setUserAnswer(userAnswer != null ? userAnswer : "");

            answerDetails.add(detail);
        }

        QuizResult result = new QuizResult();
        result.setScore(score);
        result.setTotal(questionList.size());
        result.setDuration(duration);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        String isoTime = sdf.format(new Date());
        result.setTimestamp(isoTime);
        result.setAnswers(answerDetails);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", questionList.size());
        intent.putExtra("questions", (Serializable) questionList);
        intent.putExtra("userAnswers", userAnswers);
        intent.putExtra("duration", duration);

        startActivity(intent);

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<Void> call = apiService.saveQuizResult(result);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("QuizResult", "Submitted successfully!");
                } else {
                    Log.e("QuizResult", "Failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("QuizResult", "Error: " + t.getMessage());
            }
        });

    }


}
