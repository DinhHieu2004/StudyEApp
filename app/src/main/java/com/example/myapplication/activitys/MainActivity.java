package com.example.myapplication.activitys;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.fragments.SceneLearnFragment;
import com.example.myapplication.fragments.QuizFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_learn) {
                replaceFragment(new SceneLearnFragment());
                return true;
            } else if (itemId == R.id.nav_quiz) {
                replaceFragment(new QuizFragment());
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}
