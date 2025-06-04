package com.example.myapplication.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.AnswerDetail;
import com.example.myapplication.DTO.QuizResult;
import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.ViewHolder> {

    private List<QuizResult> quizHistoryList;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public QuizHistoryAdapter(List<QuizResult> quizHistoryList) {
        this.quizHistoryList = quizHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizResult quiz = quizHistoryList.get(position);

        // Parse timestamp
        try {
            Date date = inputFormat.parse(String.valueOf(quiz.getTimestamp()));
            holder.tvDate.setText(dateFormat.format(date));
            holder.tvTime.setText(timeFormat.format(date));
        } catch (ParseException e) {
            holder.tvDate.setText("--/--/----");
            holder.tvTime.setText("--:--");
        }

        // Set score and accuracy
        holder.tvScore.setText(quiz.getScore() + "/" + quiz.getTotal());
        int accuracy = (quiz.getScore() * 100) / quiz.getTotal();
        holder.tvAccuracy.setText(accuracy + "% chính xác");

        // Set duration
        holder.tvDuration.setText(formatDuration(quiz.getDuration()));
        holder.tvQuestionCount.setText(quiz.getAnswers().size() + " câu hỏi");

        // Clear previous answers
        holder.answersContainer.removeAllViews();

        // Add answers
        for (int i = 0; i < quiz.getAnswers().size(); i++) {
            AnswerDetail answer = quiz.getAnswers().get(i);
            View answerView = createAnswerView(holder.itemView.getContext(), answer, i + 1);
            holder.answersContainer.addView(answerView);
        }
    }

    private View createAnswerView(android.content.Context context, AnswerDetail answer, int questionNumber) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer, null);

        TextView tvQuestionNumber = view.findViewById(R.id.tv_question_number);
        TextView tvQuestion = view.findViewById(R.id.tv_question);
        TextView tvUserAnswer = view.findViewById(R.id.tv_user_answer);
        TextView tvCorrectAnswer = view.findViewById(R.id.tv_correct_answer);
        View statusIndicator = view.findViewById(R.id.status_indicator);

        boolean isCorrect = answer.getUserAnswer().equals(answer.getCorrectAnswer());

        tvQuestionNumber.setText(String.valueOf(questionNumber));
        tvQuestion.setText(answer.getQuestionText());
        tvUserAnswer.setText("Bạn chọn: " + answer.getUserAnswer());

        if (isCorrect) {
            statusIndicator.setBackgroundColor(Color.GREEN);
            tvCorrectAnswer.setVisibility(View.GONE);
            tvUserAnswer.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            statusIndicator.setBackgroundColor(Color.RED);
            tvCorrectAnswer.setVisibility(View.VISIBLE);
            tvCorrectAnswer.setText("Đáp án đúng: " + answer.getCorrectAnswer());
            tvUserAnswer.setTextColor(Color.parseColor("#D32F2F"));
            tvCorrectAnswer.setTextColor(Color.parseColor("#2E7D32"));
        }

        return view;
    }

    private String formatDuration(long duration) {
        long minutes = duration / 60000;
        long seconds = (duration % 60000) / 1000;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    public int getItemCount() {
        return quizHistoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvScore, tvAccuracy, tvDuration, tvQuestionCount;
        LinearLayout answersContainer;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvAccuracy = itemView.findViewById(R.id.tv_accuracy);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            answersContainer = itemView.findViewById(R.id.answers_container);
        }
    }
}