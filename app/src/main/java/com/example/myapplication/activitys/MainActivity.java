package com.example.myapplication.activitys;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.fragments.ProfileFragment;
import com.example.myapplication.fragments.SceneLearnFragment;
import com.example.myapplication.fragments.QuizFragment;
//import com.example.myapplication.fragments.SelectTopicFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        boolean openProfileFragment = intent.getBooleanExtra("openProfileFragment", false);
        if (openProfileFragment) {
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
            bottomNav.setSelectedItemId(R.id.nav_profile);
        }
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
            }else if (itemId == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            boolean openProfileFragment = getIntent().getBooleanExtra("openProfileFragment", false);
            if (openProfileFragment) {
                bottomNav.setSelectedItemId(R.id.nav_profile);
            } else {
                bottomNav.setSelectedItemId(R.id.nav_home);
            }
        }

        switchFragment(getIntent().getStringExtra("openFragment"));
    }
    private void switchFragment(String fragmentToOpen){

        if(fragmentToOpen != null){
            switch (fragmentToOpen){
                case "quizOption":
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new QuizFragment())
                            .commit();
                    break;
            }
        }
    }
}
