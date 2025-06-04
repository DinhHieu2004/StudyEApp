package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activitys.LessionDetailActivity;

import org.jetbrains.annotations.Nullable;
import android.widget.LinearLayout;


public class SceneLearnFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scene_learn, container, false);

        // Gắn sự kiện click cho itemCourse1
        LinearLayout itemCourse1 = view.findViewById(R.id.itemCourse1);
        itemCourse1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LessionDetailActivity.class);
            intent.putExtra("title", "Classroom Interaction");
            intent.putExtra("description", "In the classroom, students learn how to answer teacher question, discuss with classmates.");
            intent.putExtra("imageResId", R.drawable.lession2);
            startActivity(intent);
        });

        return view;
    }
}
