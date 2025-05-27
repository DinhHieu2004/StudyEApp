package com.example.myapplication.activitys;


import static androidx.core.util.TimeUtils.formatDuration;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.DTO.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResultSummary;
    private RecyclerView rvResults;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        long durationMillis = getIntent().getLongExtra("duration", 0);

        tvResultSummary = findViewById(R.id.tvResultSummary);
        rvResults = findViewById(R.id.rvResults);
        btnBack = findViewById(R.id.btnBack);

        // Lấy dữ liệu từ Intent
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);
        ArrayList<Question> questions =
                (ArrayList<Question>) getIntent().getSerializableExtra("questions");
        HashMap<Integer, String> userAnswers =
                (HashMap<Integer, String>) getIntent().getSerializableExtra("userAnswers");

        // Hiển thị tóm tắt kết quả
        tvResultSummary.setText("Kết quả: " + score + "/" + total);


        String timeText = formatDuration(durationMillis);
        tvResultSummary.append("\nThời gian làm bài: " + timeText);

        // Thiết lập RecyclerView
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        ResultAdapter adapter = new ResultAdapter(questions, userAnswers);
        rvResults.setAdapter(adapter);

        // Xử lý nút Quay lại
        btnBack.

        setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, QuizOptionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes + " phút " + seconds + " giây";
    }


    private static class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
        private List<Question> questions;
        private Map<Integer, String> userAnswers;

        public ResultAdapter(List<Question> questions, Map<Integer, String> userAnswers) {
            this.questions = questions;
            this.userAnswers = userAnswers;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Question question = questions.get(position);
            String userAnswer = userAnswers.getOrDefault(position, "Chưa trả lời");
            String correctAnswer = question.getCorrectAnswer();
            boolean isCorrect = userAnswer.equals(correctAnswer);

            holder.text1.setText("Câu " + (position + 1) + ": " + question.getQuestionText());
            holder.text2.setText("Trả lời: " + userAnswer + "\nĐáp án đúng: " + correctAnswer +
                    "\nKết quả: " + (isCorrect ? "Đúng" : "Sai"));
            holder.text2.setTextColor(isCorrect ? 0xFF4CAF50 : 0xFFF44336);
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            ViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}