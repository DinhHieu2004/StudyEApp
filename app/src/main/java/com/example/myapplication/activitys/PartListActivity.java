package com.example.myapplication.activitys;


import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.DTO.response.SentencePartResponse;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class PartListActivity extends AppCompatActivity {

    private TextView levelTitle;
    private RecyclerView partsRecyclerView;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_part);

        levelTitle = findViewById(R.id.levelTitle);
        partsRecyclerView = findViewById(R.id.partsRecyclerView);
        backButton = findViewById(R.id.backButton);

        String level = getIntent().getStringExtra("level");
        ArrayList<Parcelable> parts = getIntent().getParcelableArrayListExtra("parts_list");

        if (level != null) {
            levelTitle.setText("Cấp độ " + level + " - Các Phần");
        } else {
            levelTitle.setText("Các Phần"); // Tiêu đề mặc định
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Thiết lập RecyclerView
        partsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
}
