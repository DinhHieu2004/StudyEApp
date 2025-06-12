package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageView backArrow = findViewById(R.id.backIcon);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("openProfileFragment", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        CardView cardInfo = findViewById(R.id.cardInfo);
        cardInfo.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
        });


    }
}

