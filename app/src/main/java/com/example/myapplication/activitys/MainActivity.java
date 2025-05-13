package com.example.myapplication.activitys;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.R;

public class MainActivity extends AppCompatActivity {

    CardView cardBaiTap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardBaiTap = findViewById(R.id.cardBaiTap);

        cardBaiTap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizOptionsActivity.class);
            startActivity(intent);
        });
    }


}