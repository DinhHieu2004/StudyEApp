package com.example.myapplication.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class IntroActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ImageView startBtn=findViewById(R.id.startbtn);
        startBtn.setOnClickListener(v -> startActivity(new Intent(IntroActivity.this, MainActivity.class)));
    }
}
