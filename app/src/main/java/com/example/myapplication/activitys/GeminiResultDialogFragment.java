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

    private static final String ARG_QUESTION = "question";
    private static final String ARG_SOLUTION = "solution";

    public static GeminiResultDialogFragment newInstance(String question, String solution) {
        GeminiResultDialogFragment fragment = new GeminiResultDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION, question);
        args.putString(ARG_SOLUTION, solution);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gemini_result_dialog, container, false);

        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        TextView tvSolution = view.findViewById(R.id.tvGeminiAnswer);
        Button btnClose = view.findViewById(R.id.btnCloseDialog);

        if (getArguments() != null) {
            String question = getArguments().getString(ARG_QUESTION);
            String solution = getArguments().getString(ARG_SOLUTION);
            tvQuestion.setText(question);
            tvSolution.setText(solution);
        }

        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }
}
