package com.example.myapplication.activitys;


import static android.content.ContentValues.TAG;
import static androidx.core.util.TimeUtils.formatDuration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.request.GeminiRequest;
import com.example.myapplication.DTO.response.GeminiResponse;
import com.example.myapplication.R;
import com.example.myapplication.DTO.Question;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResultSummary;
    private RecyclerView rvResults;
    private Button btnBack, btnViewSolution;
    private ProgressBar progressBar2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        long durationMillis = getIntent().getLongExtra("duration", 0);

        tvResultSummary = findViewById(R.id.tvResultSummary);
        rvResults = findViewById(R.id.rvResults);
        btnBack = findViewById(R.id.btnBack);
        progressBar2 = findViewById(R.id.progressBar2);



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

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        ResultAdapter adapter = new ResultAdapter(questions, userAnswers, apiService, getSupportFragmentManager(), progressBar2);
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
        private ApiService apiService;
        private FragmentManager fragmentManager;
        private ProgressBar progressBar2;

        public ResultAdapter(List<Question> questions, Map<Integer, String> userAnswers, ApiService apiService, FragmentManager fragmentManager, ProgressBar progressBar2) {
            this.questions = questions;
            this.userAnswers = userAnswers;
            this.apiService = apiService;
            this.fragmentManager = fragmentManager;
            this.progressBar2 = progressBar2;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_result, parent, false);
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



            //
            holder.tvGeminiReference.setOnClickListener(v -> {
                if (question.getQuestionText() == null || userAnswer == null) {
                    Toast.makeText(holder.itemView.getContext(), "Dữ liệu câu hỏi không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar2.setVisibility(ProgressBar.VISIBLE);


                GeminiRequest request = new GeminiRequest(question.getQuestionText(), userAnswer);
                Log.i("ResultAdapter", "Request: " + request.toString());

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<GeminiResponse> call = apiService.getAnswerGemini(request);

                call.enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        progressBar2.setVisibility(ProgressBar.GONE);

                        if (response.isSuccessful() && response.body() != null && response.body().getAnswer() != null) {
                            Log.i("ResultAdapter", "Response: " + response.body().getAnswer());

                            String cleanAnswer = response.body().getAnswer().replaceAll("\\*\\*", "");
                            GeminiResultDialogFragment dialog = GeminiResultDialogFragment.newInstance(
                                    question.getQuestionText(), cleanAnswer);
                            dialog.show(fragmentManager, "GeminiResultDialog");
                        } else {
                            String errorMsg = "Lỗi khi lấy dữ liệu từ Gemini (HTTP " + response.code() + ")";
                            if (response.errorBody() != null) {
                                try {
                                    errorMsg += ": " + response.errorBody().string();
                                    Log.i("ResultAdapter", "Error body: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(holder.itemView.getContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        Log.e("ResultAdapter", "Failure: " + t.getMessage(), t);
                        Toast.makeText(holder.itemView.getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
        }
        @Override
        public int getItemCount() {
            return questions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2, tvGeminiReference;

            ViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(R.id.tvQuestion);
                text2 = itemView.findViewById(R.id.tvAnswer);
                tvGeminiReference = itemView.findViewById(R.id.tvGeminiReference);
            }
        }
    }
}