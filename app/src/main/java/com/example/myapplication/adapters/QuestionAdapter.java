package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.Question;
import com.example.myapplication.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;
    private Map<Integer, Integer> selectedAnswers = new HashMap<>();



    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;

    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);


        holder.bind(questionList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;

        TextView questionNumber;

        RadioGroup optionsGroup;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            optionsGroup = itemView.findViewById(R.id.options_group);
            questionNumber = itemView.findViewById(R.id.question_number);

        }

        public void bind(Question question, int position) {
            questionNumber.setText("Question " + (position + 1));

            questionText.setText(question.getQuestionText());
            optionsGroup.removeAllViews();

            List<String> options = question.getOptions();
            for (int i = 0; i < options.size(); i++) {
                RadioButton radioButton = new RadioButton(itemView.getContext());
                radioButton.setText(options.get(i));
                radioButton.setId(i);
                optionsGroup.addView(radioButton);
            }

            // Restore selected answer if previously selected
            if (selectedAnswers.containsKey(position)) {
                optionsGroup.check(selectedAnswers.get(position));
            }

            optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
                selectedAnswers.put(position, checkedId);
            });
        }
    }

    public HashMap<Integer, String> getUserAnswers() {
        HashMap<Integer, String> answers = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : selectedAnswers.entrySet()) {
            int questionIndex = entry.getKey();
            int selectedOptionIndex = entry.getValue();
            String answer = questionList.get(questionIndex).getOptions().get(selectedOptionIndex);
            answers.put(questionIndex, answer);
        }
        return answers;
    }

}
