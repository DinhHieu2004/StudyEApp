package com.example.myapplication.fragments;

import androidx.fragment.app.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.myapplication.activitys.QuizHistoryActivity;
import com.example.myapplication.activitys.QuizQuestionActivity;
import com.example.myapplication.activitys.StatisticsActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizFragment extends Fragment {

    private TextInputEditText edtAmount;
    private AutoCompleteTextView spinnerCategory, spinnerDifficulty, spinnerType;
    private MaterialButton btnStartQuiz;
    private ImageButton btnStatistics, btnHistory, btnPronounce;
    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayList<Integer> categoryIdList = new ArrayList<>();
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false); // Tạo layout fragment_quiz.xml

        progressBar = view.findViewById(R.id.progressBar);
        edtAmount = view.findViewById(R.id.edtAmount);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerDifficulty = view.findViewById(R.id.spinnerDifficulty);
        spinnerType = view.findViewById(R.id.spinnerType);
        btnStartQuiz = view.findViewById(R.id.btnStartQuiz);
        btnStatistics = view.findViewById(R.id.btnStatistics);
        btnHistory = view.findViewById(R.id.btnHistory);
        btnPronounce = view.findViewById(R.id.btnProunce);



        setupDifficultySpinner();
        setupTypeSpinner();
        fetchCategories();

        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StatisticsActivity.class);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizHistoryActivity.class);
            startActivity(intent);
        });

        btnPronounce.setOnClickListener(v ->{
            loadFragment(new PronounceFragment());
        });

        btnStartQuiz.setOnClickListener(v -> {
            String amount = edtAmount.getText().toString().trim();
            if (amount.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập số lượng câu hỏi", Toast.LENGTH_SHORT).show();
                return;
            }


            int selectedCategoryPosition = getCategoryPosition(spinnerCategory.getText().toString());
            int categoryId = (selectedCategoryPosition != -1) ? categoryIdList.get(selectedCategoryPosition) : 0;

            String difficulty = spinnerDifficulty.getText().toString();
            String type = spinnerType.getText().toString();

            QuestionFetchRequest request = new QuestionFetchRequest(
                    amount,
                    difficulty.toLowerCase(),
                    String.valueOf(categoryId)
            );

            progressBar.setVisibility(View.VISIBLE);
            btnStartQuiz.setEnabled(false);

            ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
            Call<List<OpenTriviaQuestionResponse>> call = apiService.fetchQuestions(request);

            call.enqueue(new Callback<List<OpenTriviaQuestionResponse>>() {
                @Override
                public void onResponse(Call<List<OpenTriviaQuestionResponse>> call, Response<List<OpenTriviaQuestionResponse>> response) {
                    progressBar.setVisibility(View.GONE);
                    btnStartQuiz.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        List<Question> questions = convertToQuestions(response.body());
                        Intent intent = new Intent(getActivity(), QuizQuestionActivity.class);
                        intent.putExtra("questions", new ArrayList<>(questions));
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Không lấy được câu hỏi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<OpenTriviaQuestionResponse>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnStartQuiz.setEnabled(true);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,  fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, difficultyValues);
        spinnerDifficulty.setAdapter(adapter);
        spinnerDifficulty.setText(difficultyValues[0], false);
    }

    private void setupTypeSpinner() {
        String[] typeValues = {"multiple", "boolean"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, typeValues);
        spinnerType.setAdapter(adapter);
        spinnerType.setText(typeValues[0], false);
    }

    private void fetchCategories() {
        String url = "https://opentdb.com/api_category.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (!isAdded()) return;

                    try {
                        categoryList.add("Trộn");
                        categoryIdList.add(0);

                        JSONArray categoryArray = response.getJSONArray("trivia_categories");
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject cat = categoryArray.getJSONObject(i);
                            categoryList.add(cat.getString("name"));
                            categoryIdList.add(cat.getInt("id"));
                        }

                        Context context = getContext();
                        if (context != null) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    context,
                                    android.R.layout.simple_dropdown_item_1line,
                                    categoryList
                            );

                            spinnerCategory.setAdapter(adapter);
                            spinnerCategory.setText(categoryList.get(0), false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (getContext() != null)
                            Toast.makeText(getContext(), "Lỗi khi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (getContext() != null)
                        Toast.makeText(getContext(), "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

}
