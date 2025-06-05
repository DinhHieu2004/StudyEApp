package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.DTO.response.DialogResponse;
import com.example.myapplication.DTO.response.LessionResponse;
import com.example.myapplication.R;
import com.example.myapplication.services.DialogApiService;
import com.example.myapplication.utils.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LessionDetailActivity extends AppCompatActivity {

    private boolean isExpanded = false;
    private TextView textDialogue, btnMore;
    private TextView textTitle, textDescription;
    private ImageView imageCourse;
    private TextView tagTopic, tagLevel;

    private Long lessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_detail);

        // Nhận LessionResponse từ Intent
        LessionResponse lession = (LessionResponse) getIntent().getSerializableExtra("lession");
        if (lession == null) {
            finish(); // không có dữ liệu thì thoát
            return;
        }

        // Gán dữ liệu bài học
        lessionId = lession.getId();
        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        imageCourse = findViewById(R.id.imageCourse);
        tagTopic = findViewById(R.id.textTagTopic);
        tagLevel = findViewById(R.id.textTagLevel);

        textTitle.setText(lession.getTitle());
        textDescription.setText(lession.getDescription());
        tagTopic.setText(lession.getTopicName());
        tagLevel.setText(lession.getLevel());

        Glide.with(this)
                .load(lession.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(imageCourse);

        // Xử lý nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish(); // trở lại màn trước
        });

        // Xử lý More / Less cho đoạn hội thoại
        textDialogue = findViewById(R.id.textDialogue);
        btnMore = findViewById(R.id.btnMore);

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

        // Gọi API để lấy danh sách hội thoại
        fetchDialogPreview(lessionId);
    }

    private void fetchDialogPreview(Long lessionId) {
        Retrofit retrofit = ApiClient.getClient(); // bạn đã có ApiClient
        DialogApiService api = retrofit.create(DialogApiService.class);

        api.getDialogsByLessionId(lessionId).enqueue(new Callback<List<DialogResponse>>() {
            @Override
            public void onResponse(Call<List<DialogResponse>> call, Response<List<DialogResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DialogResponse> dialogs = response.body();

                    // Ghép đoạn hội thoại preview
                    StringBuilder preview = new StringBuilder();
                    for (DialogResponse d : dialogs) {
                        preview.append(d.getSpeaker())
                                .append(": ")
                                .append(d.getContent())
                                .append("\n");
                    }

                    textDialogue.setText(preview.toString().trim());
                }
            }

            @Override
            public void onFailure(Call<List<DialogResponse>> call, Throwable t) {
                Log.e("DialogLoad", "Failed to load dialogs", t);
            }
        });
    }
}


