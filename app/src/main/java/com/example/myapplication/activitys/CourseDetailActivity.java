package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class CourseDetailActivity extends AppCompatActivity {

    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_detail);

        TextView textDialogue = findViewById(R.id.textDialogue);
        TextView btnMore = findViewById(R.id.btnMore);

        btnMore.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            if (isExpanded) {
                textDialogue.setMaxLines(Integer.MAX_VALUE);
                btnMore.setText("Less");
            } else {
                textDialogue.setMaxLines(4);
                btnMore.setText("More...");
            }
        });

        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CourseDetailActivity.this, SceneLearnActivity.class);
            intent.putExtra("fragment_to_show", "scene_learn");
            startActivity(intent);
            finish();
        });
    }
}

