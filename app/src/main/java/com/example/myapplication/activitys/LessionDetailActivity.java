package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.DTO.response.DialogResponse;
import com.example.myapplication.DTO.response.LessionResponse;
import com.example.myapplication.R;
import com.example.myapplication.services.DialogApiService;
import com.example.myapplication.utils.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private TextToSpeech tts;
    private boolean isSpeaking = false;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;

    private SeekBar audioSeekBar;
    private TextView audioTime;
    private Button btnVocabulary, btnPractice;

    private ImageButton btnAudioPlay;

    private Long lessionId;

    private List<DialogLine> dialogLines = new ArrayList<>();

    private float currentPitch = 1.2f;


    private static class DialogLine {
        String speaker;
        String content;

        DialogLine(String speaker, String content) {
            this.speaker = speaker;
            this.content = content;
        }
    }

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
        btnVocabulary = findViewById(R.id.btnVocabulary);
        btnPractice = findViewById(R.id.btnPractice);
        btnAudioPlay = findViewById(R.id.btnAudioPlay);
        audioSeekBar = findViewById(R.id.audioSeekBar);
        audioTime = findViewById(R.id.audioTime);
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

        // Xử lý btn học từ vựng
        btnVocabulary.setOnClickListener(v -> {
            Intent intent = new Intent(this, VocabularyActivity.class);
            intent.putExtra("lessionId", lessionId);
            startActivity(intent);
        });

        btnPractice.setOnClickListener(v -> {
            Toast.makeText(this, "Practice feature coming soon...", Toast.LENGTH_SHORT).show();
        });

        // text to speak dialog
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        // Xử lý khi bấm nút Play/Pause
        btnAudioPlay.setOnClickListener(v -> {
            if (isSpeaking) {
                tts.stop();
                isSpeaking = false;
                btnAudioPlay.setImageResource(R.drawable.play_record);
            } else {
                if (!dialogLines.isEmpty()) {
                    isSpeaking = true;
                    btnAudioPlay.setImageResource(R.drawable.pause_record);
                    speakDialogLinesSequentially(dialogLines, 0);
                } else {
                    Toast.makeText(this, "No dialogue found to speak", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateSeekBar(int totalDuration) {
        updateSeekBarRunnable = new Runnable() {
            int currentProgress = 0;

            @Override
            public void run() {
                if (isSpeaking && currentProgress <= totalDuration) {
                    audioSeekBar.setProgress(currentProgress);
                    audioTime.setText(formatTime(currentProgress));
                    currentProgress += 500;
                    handler.postDelayed(this, 500);
                } else {
                    isSpeaking = false;
                    btnAudioPlay.setImageResource(R.drawable.pause_record);
                    audioSeekBar.setProgress(0);
                    audioTime.setText("00:00");
                }
            }
        };
        handler.post(updateSeekBarRunnable);
    }

    private String formatTime(int millis) {
        int minutes = millis / 60000;
        int seconds = (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private int estimateDuration(String text) {
        int wordCount = text.trim().split("\\s+").length;
        return wordCount * 500;
    }

    private void speakDialogLinesSequentially(List<DialogLine> lines, int index) {
        if (index >= lines.size()) {
            isSpeaking = false;
            btnAudioPlay.setImageResource(R.drawable.play_record);
            return;
        }

        DialogLine line = lines.get(index);

        if (index > 0) {
            String prevSpeaker = lines.get(index - 1).speaker;
            if (!line.speaker.equalsIgnoreCase(prevSpeaker)) {
                currentPitch = (currentPitch == 1.2f) ? 0.9f : 1.2f;
            }
        }

        tts.setPitch(currentPitch);
        tts.speak(line.content, TextToSpeech.QUEUE_FLUSH, null, "LINE_" + index);

        int duration = estimateDuration(line.content);
        handler.postDelayed(() -> speakDialogLinesSequentially(lines, index + 1), duration + 500);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (handler != null && updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
        super.onDestroy();
    }


    private void fetchDialogPreview(Long lessionId) {
        Retrofit retrofit = ApiClient.getClient(this);
        DialogApiService api = retrofit.create(DialogApiService.class);

        api.getDialogsByLessionId(lessionId).enqueue(new Callback<List<DialogResponse>>() {
            @Override
            public void onResponse(Call<List<DialogResponse>> call, Response<List<DialogResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DialogResponse> dialogs = response.body();
                    dialogLines.clear();

                    StringBuilder preview = new StringBuilder();
                    for (DialogResponse d : dialogs) {
                        dialogLines.add(new DialogLine(d.getSpeaker(), d.getContent()));
                        preview.append(d.getSpeaker()).append(": ").append(d.getContent()).append("\n");
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


