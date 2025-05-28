package com.example.myapplication.activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout btn1 = findViewById(R.id.mainLayout);
        btn1.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CoursesListActivity.class))
        );

        // Hỗ trợ layout toàn màn hình và tránh che bottom nav
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        View bottomNav = findViewById(R.id.bottomNavigationView);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
            v.setPadding(0, 0, 0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });
    }

}