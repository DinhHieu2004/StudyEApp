package com.example.myapplication.activitys;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DTO.response.OpenTriviaQuestionResponse;
import com.example.myapplication.R;

import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;


public class QuizQuestionActivity extends AppCompatActivity {

    private TextView tvQuestionNumber, tvCategory, tvDifficulty;
    private TextView tvQuestion1, tvQuestion2;
    private RadioGroup radioGroup1, radioGroup2;
    private RadioButton[] rbGroup1, rbGroup2;
    private Button btnPrevious, btnNext;

    private List<Question> questionList;
    private int currentPage = 0;
    private final int questionsPerPage = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);

        initViews();

        // Nhận dữ liệu từ Intent
        ArrayList<OpenTriviaQuestionResponse> triviaQuestions =
                (ArrayList<OpenTriviaQuestionResponse>) getIntent().getSerializableExtra("questions");

        if (triviaQuestions != null && !triviaQuestions.isEmpty()) {
            questionList = new ArrayList<>();
            for (OpenTriviaQuestionResponse q : triviaQuestions) {
                // Giải mã nếu bị encode HTML
                String questionText = StringEscapeUtils.unescapeHtml4(q.getQuestion());
                String category = q.getCategory();
                String difficulty = q.getDifficulty();

                List<String> options = new ArrayList<>(q.getIncorrectAnswers());
                options.add(q.getCorrectAnswer());  // Thêm đáp án đúng

                // Trộn thứ tự các đáp án (shuffle)
                java.util.Collections.shuffle(options);

                questionList.add(new Question(questionText, category, difficulty, options));
            }
        } else {
            loadSampleQuestions(); // fallback
        }

        loadQuestionsForPage(currentPage);

        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                loadQuestionsForPage(currentPage);
            }
        });

        btnNext.setOnClickListener(v -> {
            if ((currentPage + 1) * questionsPerPage < questionList.size()) {
                currentPage++;
                loadQuestionsForPage(currentPage);
            }
        });
    }


    private void initViews() {
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvCategory = findViewById(R.id.tvCategory);
        tvDifficulty = findViewById(R.id.tvDifficulty);

        tvQuestion1 = findViewById(R.id.tvQuestion1);
        tvQuestion2 = findViewById(R.id.tvQuestion2);

        radioGroup1 = findViewById(R.id.radioGroupAnswers1);
        radioGroup2 = findViewById(R.id.radioGroupAnswers2);

        rbGroup1 = new RadioButton[]{
                findViewById(R.id.rbAnswer1_1),
                findViewById(R.id.rbAnswer1_2),
                findViewById(R.id.rbAnswer1_3),
                findViewById(R.id.rbAnswer1_4)
        };

        rbGroup2 = new RadioButton[]{
                findViewById(R.id.rbAnswer2_1),
                findViewById(R.id.rbAnswer2_2),
                findViewById(R.id.rbAnswer2_3),
                findViewById(R.id.rbAnswer2_4)
        };

        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
    }

    private void loadQuestionsForPage(int page) {
        int startIndex = page * questionsPerPage;
        int endIndex = Math.min(startIndex + questionsPerPage, questionList.size());

        tvQuestionNumber.setText("Question " + (startIndex + 1) + "-" + endIndex + " / " + questionList.size());
        tvCategory.setText("Category: " + questionList.get(startIndex).getCategory());
        tvDifficulty.setText("Difficulty: " + questionList.get(startIndex).getDifficulty());

        if (startIndex < questionList.size()) {
            Question q1 = questionList.get(startIndex);
            tvQuestion1.setText(q1.getQuestionText());
            for (int i = 0; i < 4; i++) {
                rbGroup1[i].setText(q1.getOptions().get(i));
                rbGroup1[i].setChecked(false);
            }
        }

        if (startIndex + 1 < questionList.size()) {
            Question q2 = questionList.get(startIndex + 1);
            tvQuestion2.setText(q2.getQuestionText());
            for (int i = 0; i < 4; i++) {
                rbGroup2[i].setText(q2.getOptions().get(i));
                rbGroup2[i].setChecked(false);
            }

            tvQuestion2.setVisibility(View.VISIBLE);
            radioGroup2.setVisibility(View.VISIBLE);
        } else {
            tvQuestion2.setVisibility(View.GONE);
            radioGroup2.setVisibility(View.GONE);
        }
    }

    private void loadSampleQuestions() {
        questionList = new ArrayList<>();
        // Demo 5 câu hỏi
        for (int i = 1; i <= 5; i++) {
            List<String> options = new ArrayList<>();
            options.add("Answer A");
            options.add("Answer B");
            options.add("Answer C");
            options.add("Answer D");
            questionList.add(new Question("Question " + i + " content?", "Category", "Medium", options));
        }
    }

    // Class mẫu đại diện cho 1 câu hỏi
    public static class Question {
        private String questionText, category, difficulty;
        private List<String> options;

        public Question(String questionText, String category, String difficulty, List<String> options) {
            this.questionText = questionText;
            this.category = category;
            this.difficulty = difficulty;
            this.options = options;
        }

        public String getQuestionText() { return questionText; }
        public String getCategory() { return category; }
        public String getDifficulty() { return difficulty; }
        public List<String> getOptions() { return options; }
    }
}
