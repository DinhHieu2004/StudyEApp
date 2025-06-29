package com.example.myapplication.activitys;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.DTO.Question;
import com.example.myapplication.DTO.request.QuestionFetchRequest;
import com.example.myapplication.DTO.response.OpenTriviaQuestionResponse;
import com.example.myapplication.R;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizOptionsActivity extends AppCompatActivity {
    private TextInputEditText edtAmount;
    private AutoCompleteTextView spinnerCategory, spinnerDifficulty, spinnerType;
    private MaterialButton btnStartQuiz;
    private ImageButton btnStatistics, btnHistory, btnPronounce;
    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayList<Integer> categoryIdList = new ArrayList<>();
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_options);

        progressBar = findViewById(R.id.progressBar);

        edtAmount = findViewById(R.id.edtAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        spinnerType = findViewById(R.id.spinnerType);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);

        btnPronounce = findViewById(R.id.btnProunce);
        btnHistory = findViewById(R.id.btnHistory);

        btnStatistics = findViewById(R.id.btnStatistics);


        setupDifficultySpinner();
        setupTypeSpinner();
        fetchCategories();

        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(QuizOptionsActivity.this, StatisticsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(QuizOptionsActivity.this, QuizHistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnStartQuiz.setOnClickListener(v -> {
            String amount = edtAmount.getText().toString().trim();
            if (amount.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedCategoryPosition = getCategoryPosition(spinnerCategory.getText().toString());
            int categoryId = (selectedCategoryPosition != -1) ?
                    categoryIdList.get(selectedCategoryPosition) : 0;

            String difficulty = spinnerDifficulty.getText().toString();
            String type = spinnerType.getText().toString();

            QuestionFetchRequest request = new QuestionFetchRequest(
                    amount,
                    difficulty.toLowerCase(),
                    String.valueOf(categoryId)
            );
            Log.i(TAG, "onCreate: "+ request);

            // Hiển thị ProgressBar
            progressBar.setVisibility(ProgressBar.VISIBLE);
            // Vô hiệu hóa nút Start Quiz để tránh nhấn nhiều lần
            btnStartQuiz.setEnabled(false);


            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
            Call<List<OpenTriviaQuestionResponse>> call  = apiService.fetchQuestions(request);

            call.enqueue(new Callback<List<OpenTriviaQuestionResponse>>() {
                @Override
                public void onResponse(Call<List<OpenTriviaQuestionResponse>> call, Response<List<OpenTriviaQuestionResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {


                        // Ẩn ProgressBar
                        progressBar.setVisibility(ProgressBar.GONE);
                        // Kích hoạt lại nút Start Quiz
                        btnStartQuiz.setEnabled(true);

                        List<OpenTriviaQuestionResponse> questions = response.body();

                        Log.d("API_Response", "Data: " + questions);


                        List<Question> questionsToSend = convertToQuestions(questions);
                        Intent intent = new Intent(QuizOptionsActivity.this, QuizQuestionActivity.class);
                        intent.putExtra("questions", new ArrayList<>(questionsToSend)); // Gửi đúng kiểu
                        startActivity(intent);
                    } else {
                        Log.e("API_Response", "Error: " + response.errorBody().toString());

                        Toast.makeText(QuizOptionsActivity.this, "Không lấy được câu hỏi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<OpenTriviaQuestionResponse>> call, Throwable t) {
                    // Ẩn ProgressBar
                    progressBar.setVisibility(ProgressBar.GONE);
                    // Kích hoạt lại nút Start Quiz
                    btnStartQuiz.setEnabled(true);

                    Toast.makeText(QuizOptionsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        });
    }


    private List<Question> convertToQuestions(List<OpenTriviaQuestionResponse> responseList) {
        List<Question> result = new ArrayList<>();
        for (OpenTriviaQuestionResponse r : responseList) {
            List<String> options = new ArrayList<>(r.getIncorrectAnswers());
            options.add(r.getCorrectAnswer());
            Collections.shuffle(options);

            result.add(new Question(r.getQuestion(), options, r.getCorrectAnswer(), r.getCategory()));
        }
        return result;
    }


    private int getCategoryPosition(String categoryName) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).equals(categoryName)) {
                return i;
            }
        }
        return -1;
    }

    private void setupDifficultySpinner() {
        String[] difficultyValues = {"easy", "medium", "hard"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, difficultyValues);
        spinnerDifficulty.setAdapter(adapter);

        spinnerDifficulty.setText(difficultyValues[0], false);
    }

    private void setupTypeSpinner() {
        String[] typeValues = {"multiple", "boolean"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, typeValues);
        spinnerType.setAdapter(adapter);

        spinnerType.setText(typeValues[0], false);
    }

    private void fetchCategories() {
        String url = "https://opentdb.com/api_category.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        categoryList.add("Trộn");
                        categoryIdList.add(0);

                        JSONArray categoryArray = response.getJSONArray("trivia_categories");
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject cat = categoryArray.getJSONObject(i);
                            categoryList.add(cat.getString("name"));
                            categoryIdList.add(cat.getInt("id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_dropdown_item_1line, categoryList);
                        spinnerCategory.setAdapter(adapter);

                        spinnerCategory.setText(categoryList.get(0), false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}