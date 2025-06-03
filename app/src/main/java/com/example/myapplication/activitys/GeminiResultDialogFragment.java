package com.example.myapplication.activitys;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.myapplication.R;


public class GeminiResultDialogFragment extends DialogFragment {

    private static final String ARG_QUESTION = "arg_question";
    private static final String ARG_ANSWER = "arg_answer";

    public static GeminiResultDialogFragment newInstance(String question, String geminiAnswer) {
        GeminiResultDialogFragment fragment = new GeminiResultDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION, question);
        args.putString(ARG_ANSWER, geminiAnswer);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gemini_result_dialog, container, false);

        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        TextView tvGeminiAnswer = view.findViewById(R.id.tvGeminiAnswer);
        Button btnClose = view.findViewById(R.id.btnCloseDialog);

        Bundle args = getArguments();
        if (args != null) {
            String question = args.getString(ARG_QUESTION);
            String geminiAnswer = args.getString(ARG_ANSWER);

            tvQuestion.setText(question);
            tvGeminiAnswer.setText(geminiAnswer);
        }

        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // Set kích thước dialog nếu muốn
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
