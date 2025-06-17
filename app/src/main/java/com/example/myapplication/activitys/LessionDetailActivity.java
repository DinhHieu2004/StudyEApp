package com.example.myapplication.activitys;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.myapplication.services.LessionApiService;
import com.example.myapplication.utils.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LessionDetailActivity extends AppCompatActivity {

    private TextView textDialogue, btnMore;
    private TextView textTitle, textDescription;
    private ImageView imageCourse;
    private TextView tagTopic, tagLevel;
    private ImageButton btnAudioPlay;
    private SeekBar audioSeekBar;
    private TextView audioTime;
    private Button btnVocabulary, btnPractice;

    private boolean isExpanded = false;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;

    private Long lessionId;
    private MediaPlayer mediaPlayer;

    private List<DialogLine> dialogLines = new ArrayList<>();

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

        // View mapping
        textDialogue = findViewById(R.id.textDialogue);
        btnMore = findViewById(R.id.btnMore);
        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        imageCourse = findViewById(R.id.imageCourse);
        tagTopic = findViewById(R.id.textTagTopic);
        tagLevel = findViewById(R.id.textTagLevel);
        btnAudioPlay = findViewById(R.id.btnAudioPlay);
        audioSeekBar = findViewById(R.id.audioSeekBar);
        audioTime = findViewById(R.id.audioTime);
        btnVocabulary = findViewById(R.id.btnVocabulary);
        btnPractice = findViewById(R.id.btnPractice);

        LessionResponse lession = (LessionResponse) getIntent().getSerializableExtra("lession");
        String audioUrl = lession.getAudioUrl();

        if (lession == null) {
            finish();
            return;
        }

        lessionId = lession.getId();
        textTitle.setText(lession.getTitle());
        textDescription.setText(lession.getDescription());
        tagTopic.setText(lession.getTopicName());
        tagLevel.setText(lession.getLevel());

        Glide.with(this)
                .load(lession.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(imageCourse);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnMore.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            textDialogue.setMaxLines(isExpanded ? Integer.MAX_VALUE : 4);
            btnMore.setText(isExpanded ? "Less" : "More...");
        });

        fetchDialogPreview(lessionId);

        btnVocabulary.setOnClickListener(v -> {
            LessionApiService apiService = ApiClient.getClient(this).create(LessionApiService.class);
            apiService.markLessonWatched(lessionId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("Lesson", "Đã lưu bài học đã xem thành công");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("Lesson", "Lỗi khi lưu bài học đã xem", t);
                }
            });
            Intent intent = new Intent(this, VocabularyActivity.class);
            intent.putExtra("lessionId", lessionId);
            startActivity(intent);
        });

        btnPractice.setOnClickListener(v -> Toast.makeText(this, "Practice feature coming soon...", Toast.LENGTH_SHORT).show());

        btnAudioPlay.setOnClickListener(v -> {
            if (mediaPlayer == null) {
                if (audioUrl == null || audioUrl.isEmpty()) {
                    Toast.makeText(this, "Không có audio cho bài học này", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri audioUri = Uri.parse(audioUrl);
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(this, audioUri);
                    mediaPlayer.setOnPreparedListener(mp -> {
                        audioSeekBar.setMax(mediaPlayer.getDuration());
                        mediaPlayer.start();
                        isPlaying = true;
                        btnAudioPlay.setImageResource(R.drawable.pause_record);
                        startSeekBarTimer();
                    });
                    mediaPlayer.setOnCompletionListener(mp -> {
                        btnAudioPlay.setImageResource(R.drawable.play_record);
                        isPlaying = false;
                    });
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Could not play audio", Toast.LENGTH_SHORT).show();
                }
            } else if (isPlaying) {
                mediaPlayer.pause();
                isPlaying = false;
                btnAudioPlay.setImageResource(R.drawable.play_record);
            } else {
                mediaPlayer.start();
                isPlaying = true;
                btnAudioPlay.setImageResource(R.drawable.pause_record);
                startSeekBarTimer();
            }
        });

        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    audioTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        });

    }

    private void startSeekBarTimer() {
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPos = mediaPlayer.getCurrentPosition();
                    audioSeekBar.setProgress(currentPos);
                    audioTime.setText(formatTime(currentPos));
                    handler.postDelayed(this, 500);
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

    private void fetchDialogPreview(Long lessionId) {
        Retrofit retrofit = ApiClient.getClient(this);
        DialogApiService api = retrofit.create(DialogApiService.class);

        api.getDialogsByLessionId(lessionId).enqueue(new Callback<List<DialogResponse>>() {
            @Override
            public void onResponse(Call<List<DialogResponse>> call, Response<List<DialogResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dialogLines.clear();
                    StringBuilder preview = new StringBuilder();
                    for (DialogResponse d : response.body()) {
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

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (handler != null && updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
        super.onDestroy();
    }
}
