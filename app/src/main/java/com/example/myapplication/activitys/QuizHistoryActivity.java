package com.example.myapplication.activitys;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.AnswerDetail;
import com.example.myapplication.DTO.QuizResult;
import com.example.myapplication.R;
import com.example.myapplication.adapters.QuizHistoryAdapter;
import com.example.myapplication.repositoris.QuizHistoryRepository;
import com.example.myapplication.services.ApiService;

import com.example.myapplication.utils.ApiClient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizHistoryActivity extends AppCompatActivity {

    private TextView tvStartDate, tvEndDate;
    private Button btnFilter, btnApplyFilter;
    private LinearLayout filterLayout, statsLayout;
    private TextView tvTotalQuizzes, tvTotalCorrect, tvTotalQuestions, tvAccuracy;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoData;

    private QuizHistoryAdapter adapter;
    private List<QuizResult> quizHistoryList;
    private ApiService apiService;

    private String startDate = "2025-05-26";
    private String endDate = "2025-05-29";
    private boolean isFilterVisible = false;
    private QuizHistoryRepository quizHistoryRepository;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_history);

        initViews();
        setupRecyclerView();
        setupClickListeners();

        quizHistoryRepository = new QuizHistoryRepository(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);
        fetchQuizHistory();
    }

    private void initViews() {
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        btnFilter = findViewById(R.id.btn_filter);
        btnApplyFilter = findViewById(R.id.btn_apply_filter);
        filterLayout = findViewById(R.id.filter_layout);
        statsLayout = findViewById(R.id.stats_layout);
        tvTotalQuizzes = findViewById(R.id.tv_total_quizzes);
        tvTotalCorrect = findViewById(R.id.tv_total_correct);
        tvTotalQuestions = findViewById(R.id.tv_total_questions);
        tvAccuracy = findViewById(R.id.tv_accuracy);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);

        // Set initial dates
        updateDateDisplays();
    }

    private void setupRecyclerView() {
        quizHistoryList = new ArrayList<>();
        adapter = new QuizHistoryAdapter(quizHistoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnFilter.setOnClickListener(v -> toggleFilter());
        btnApplyFilter.setOnClickListener(v -> fetchQuizHistory());

        tvStartDate.setOnClickListener(v -> showDatePicker(true));
        tvEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void toggleFilter() {
        isFilterVisible = !isFilterVisible;
        filterLayout.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);
        btnFilter.setText(isFilterVisible ? "Ẩn bộ lọc" : "Hiện bộ lọc");
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = dateFormat.format(calendar.getTime());

                    if (isStartDate) {
                        startDate = selectedDate;
                    } else {
                        endDate = selectedDate;
                    }
                    updateDateDisplays();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateDateDisplays() {
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            tvStartDate.setText(displayDateFormat.format(start));
            tvEndDate.setText(displayDateFormat.format(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
    private void fetchQuizHistory() {
        showLoading(true);

        Call<List<QuizResult>> call = apiService.getQuizHistory(startDate, endDate);

        call.enqueue(new Callback<List<QuizResult>>() {
            @Override
            public void onResponse(Call<List<QuizResult>> call, Response<List<QuizResult>> response) {
                showLoading(false);

                Log.i(TAG, "onResponse: "+ response.body());
                if (response.isSuccessful() && response.body() != null) {
                    quizHistoryList.clear();
                    quizHistoryList.addAll(response.body());

                    updateStats();
                    adapter.notifyDataSetChanged();

                    tvNoData.setVisibility(quizHistoryList.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(quizHistoryList.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    Toast.makeText(QuizHistoryActivity.this,
                            "Lỗi khi tải dữ liệu: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuizResult>> call, Throwable t) {
                showLoading(false);

                Log.i(TAG, "onFailure: "+ t.getMessage());
                Toast.makeText(QuizHistoryActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

     **/

    private void fetchQuizHistory() {
        showLoading(true);

        quizHistoryRepository.loadQuizHistory(startDate, endDate, new QuizHistoryRepository.QuizHistoryCallback() {
            @Override
            public void onSuccess(List<QuizResult> results, boolean isFromCache) {
                runOnUiThread(() -> {
                    showLoading(false);
                    updateDataSourceIndicator(isFromCache); // Optional: hiển thị indicator online/offline

                    quizHistoryList.clear();
                    quizHistoryList.addAll(results);

                    updateStats();
                    adapter.notifyDataSetChanged();

                    tvNoData.setVisibility(quizHistoryList.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(quizHistoryList.isEmpty() ? View.GONE : View.VISIBLE);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(QuizHistoryActivity.this, error, Toast.LENGTH_SHORT).show();

                    // Show no data state
                    tvNoData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                });
            }
        });
    }

    // Optional: Method để hiển thị data source indicator
    private void updateDataSourceIndicator(boolean isFromCache) {
        // Có thể thêm TextView hoặc indicator để user biết data từ đâu
        if (isFromCache) {
            // Show offline indicator
            Log.d(TAG, "Data loaded from offline cache");
        } else {
            // Show online indicator
            Log.d(TAG, "Data loaded from server");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (quizHistoryRepository != null) {
            quizHistoryRepository.cleanup();
        }
    }
    private void updateStats() {
        int totalQuizzes = quizHistoryList.size();
        int totalQuestions = 0;
        int totalCorrect = 0;

        for (QuizResult quiz : quizHistoryList) {
            totalQuestions += quiz.getTotal();
            totalCorrect += quiz.getScore();
        }

        int accuracy = totalQuestions > 0 ? (totalCorrect * 100) / totalQuestions : 0;

        tvTotalQuizzes.setText(String.valueOf(totalQuizzes));
        tvTotalCorrect.setText(String.valueOf(totalCorrect));
        tvTotalQuestions.setText(String.valueOf(totalQuestions));
        tvAccuracy.setText(accuracy + "%");
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        statsLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}