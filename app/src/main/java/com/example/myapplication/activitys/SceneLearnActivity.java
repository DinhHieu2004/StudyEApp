package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.fragments.SceneLearnFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import android.view.MenuItem;

public class SceneLearnActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_learn);

        bottomNav = findViewById(R.id.bottomNavigationView);

        // Hiển thị fragment ban đầu (SceneLearn hoặc Home)
        handleInitialFragment();

        // Điều hướng nav menu
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_quiz) {
                startActivity(new Intent(this, QuizOptionsActivity.class));
                return true;
            } else if (id == R.id.nav_learn) {
                replaceFragment(new SceneLearnFragment());
                return true;
            }
            return false;
        });
    }

    private void handleInitialFragment() {
        String fragToShow = getIntent().getStringExtra("fragment_to_show");

        if ("scene_learn".equals(fragToShow)) {
            replaceFragment(new SceneLearnFragment());
            bottomNav.setSelectedItemId(R.id.nav_learn);
        } else {
            replaceFragment(new HomeFragment());
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void replaceFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}