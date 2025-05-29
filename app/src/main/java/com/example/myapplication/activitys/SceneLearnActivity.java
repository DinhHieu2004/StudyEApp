package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import android.view.MenuItem;

public class SceneLearnActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_learn);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_learn);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(SceneLearnActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_learn) {
                return true;
            } else if (id == R.id.nav_quiz) {
                startActivity(new Intent(SceneLearnActivity.this, QuizOptionsActivity.class));
                return true;
            }
            return false;
        });
    }
}
