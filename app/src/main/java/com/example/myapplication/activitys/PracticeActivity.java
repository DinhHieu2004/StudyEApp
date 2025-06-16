package com.example.myapplication.activitys;


import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.widget.Toast;

import com.example.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class PracticeActivity extends AppCompatActivity {

    private TextView levelBadge;
    private TextView practiceTitle;
    private TextView progressText;
    private ProgressBar lessonProgress;
    private TextView practiceText;
    private TextView phoneticText;
    private LinearLayout listenButton;
    private LinearLayout recordButton;
    private ImageView recordIcon;
    private TextView recordText;
    private LinearLayout recordingStatus;
    private TextView recordingTimer;
    private CardView resultCard;
    private TextView scoreText;
    private TextView feedbackText;
    private LinearLayout playRecordedButton;
    private Button tryAgainButton;
    private Button nextButton;

    // Audio related
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private Timer recordingTimerObj;
    private int recordingSeconds = 0;

    // Data
    private String currentLevel;
    private int sentenceId;
    private String sentenceContent;
    private String phoneticTranscription;

    // Text-to-Speech
    private TextToSpeech textToSpeech;
    private boolean isTTSReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pronounce_practive);

        initViews();
        getIntentData();
        setupTextToSpeech();
        setupClickListeners();
        loadSentenceData();

        // Request audio permissions
        requestAudioPermissions();
    }

    private void initViews() {
        levelBadge = findViewById(R.id.levelBadge);
        practiceTitle = findViewById(R.id.practiceTitle);
        progressText = findViewById(R.id.progressText);
        lessonProgress = findViewById(R.id.lessonProgress);
        practiceText = findViewById(R.id.practiceText);
        phoneticText = findViewById(R.id.phoneticText);
        listenButton = findViewById(R.id.listenButton);
        recordButton = findViewById(R.id.recordButton);
        recordIcon = findViewById(R.id.recordIcon);
        recordText = findViewById(R.id.recordText);
        recordingStatus = findViewById(R.id.recordingStatus);
        recordingTimer = findViewById(R.id.recordingTimer);
      //  resultCard = findViewById(R.id.resultCard);
        scoreText = findViewById(R.id.scoreText);
      //  feedbackText = findViewById(R.id.feedbackText);
      //  playRecordedButton = findViewById(R.id.playRecordedButton);
        tryAgainButton = findViewById(R.id.tryAgainButton);
        nextButton = findViewById(R.id.nextButton);
    }

    private void getIntentData() {
        currentLevel = getIntent().getStringExtra("level");
        sentenceId = getIntent().getIntExtra("sentence_id", 0);
        sentenceContent = getIntent().getStringExtra("sentence_content");

        // Set level badge
        if (currentLevel != null) {
            levelBadge.setText(currentLevel.toUpperCase());
        }
    }

    private void setupTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("PracticeActivity", "Language not supported");
                } else {
                    isTTSReady = true;
                    textToSpeech.setSpeechRate(0.8f); // Slower for learning
                }
            }
        });
    }

    private void setupClickListeners() {
        listenButton.setOnClickListener(v -> playNativeAudio());
        recordButton.setOnClickListener(v -> toggleRecording());
        playRecordedButton.setOnClickListener(v -> playRecordedAudio());
        tryAgainButton.setOnClickListener(v -> resetPractice());
        nextButton.setOnClickListener(v -> goToNextSentence());
    }

    private void loadSentenceData() {
        if (sentenceContent != null) {
            practiceText.setText(sentenceContent);

            // Generate phonetic transcription (simplified)
            phoneticTranscription = generatePhoneticTranscription(sentenceContent);
            phoneticText.setText(phoneticTranscription);

            // Update progress (mock data)
            progressText.setText("Sentence " + sentenceId + " of 100");
            lessonProgress.setProgress((sentenceId * 100) / 100);
        }
    }

    private String generatePhoneticTranscription(String text) {
        // Simplified phonetic mapping - in real app, use proper phonetic API
        return "/" + text.toLowerCase()
                .replace("how", "haʊ")
                .replace("much", "mʌtʃ")
                .replace("does", "dʌz")
                .replace("this", "ðɪs")
                .replace("apple", "ˈæpəl")
                .replace("cost", "kɔːst")
                .replace("i", "aɪ")
                .replace("feel", "fiːl")
                .replace("happy", "ˈhæpi")
                .replace("have", "hæv")
                .replace("good", "ɡʊd")
                .replace("day", "deɪ") + "/";
    }

    private void playNativeAudio() {
        if (isTTSReady && !isPlaying) {
            isPlaying = true;

            // Visual feedback
            listenButton.setAlpha(0.7f);

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {}

                @Override
                public void onDone(String utteranceId) {
                    runOnUiThread(() -> {
                        isPlaying = false;
                        listenButton.setAlpha(1.0f);
                    });
                }

                @Override
                public void onError(String utteranceId) {
                    runOnUiThread(() -> {
                        isPlaying = false;
                        listenButton.setAlpha(1.0f);
                    });
                }
            });

            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sentence");
            textToSpeech.speak(sentenceContent, TextToSpeech.QUEUE_FLUSH, params, "sentence");
        }
    }

    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestAudioPermissions();
            return;
        }

        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/recorded_audio.3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            updateRecordingUI(true);
            startRecordingTimer();

        } catch (IOException e) {
            Log.e("PracticeActivity", "Recording failed", e);
            Toast.makeText(this, "Lỗi khi bắt đầu ghi âm", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                isRecording = false;
                updateRecordingUI(false);
                stopRecordingTimer();

                // Analyze pronunciation (mock)
                analyzePronunciation();

            } catch (RuntimeException e) {
                Log.e("PracticeActivity", "Stop recording failed", e);
            }
        }
    }

    private void startRecordingTimer() {
        recordingSeconds = 0;
        recordingTimerObj = new Timer();
        recordingTimerObj.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                recordingSeconds++;
                runOnUiThread(() -> {
                    int minutes = recordingSeconds / 60;
                    int seconds = recordingSeconds % 60;
                    recordingTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                });
            }
        }, 1000, 1000);
    }

    private void stopRecordingTimer() {
        if (recordingTimerObj != null) {
            recordingTimerObj.cancel();
            recordingTimerObj = null;
        }
    }

    private void updateRecordingUI(boolean recording) {
        recordingStatus.setVisibility(recording ? View.VISIBLE : View.GONE);

        if (recording) {
            recordIcon.setImageResource(R.drawable.ic_stop);
            recordText.setText("Stop");
            recordButton.setBackgroundResource(R.drawable.bg_record_button);
        } else {
            recordIcon.setImageResource(R.drawable.ic_mic);
            recordText.setText("Record");
            recordButton.setBackgroundResource(R.drawable.bg_record_button);
        }
    }

    private void analyzePronunciation() {
        // Mock pronunciation analysis
        new Handler().postDelayed(() -> {
            int score = 75 + new Random().nextInt(20); // Random score 75-95
            String feedback = generateFeedback(score);

            showResult(score, feedback);
        }, 1000);
    }

    private String generateFeedback(int score) {
        if (score >= 90) {
            return "Excellent pronunciation! You sound very natural.";
        } else if (score >= 80) {
            return "Great job! Your pronunciation is very good.";
        } else if (score >= 70) {
            return "Good work! Try to speak a bit more clearly.";
        } else {
            return "Keep practicing! Focus on pronunciation of each word.";
        }
    }

    private void showResult(int score, String feedback) {
        scoreText.setText(score + "%");
        feedbackText.setText(feedback);

        // Set score color
     //   if (score >= 80) {
       //     scoreText.setTextColor(ContextCompat.getColor(this, R.color.green));
      //  } else if (score >= 60) {
      //      scoreText.setTextColor(ContextCompat.getColor(this, R.color.orange));
      //  } else {
      //      scoreText.setTextColor(ContextCompat.getColor(this, R.color.red));
      //  }

        // Show result card with animation
        resultCard.setVisibility(View.VISIBLE);
        resultCard.setAlpha(0f);
        resultCard.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
    }

    private void playRecordedAudio() {
        if (audioFilePath != null && new File(audioFilePath).exists()) {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioFilePath);
                mediaPlayer.prepare();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(mp -> {
                    // Audio finished playing
                });

            } catch (IOException e) {
                Log.e("PracticeActivity", "Error playing recorded audio", e);
                Toast.makeText(this, "Không thể phát audio đã ghi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetPractice() {
        // Hide result and reset UI
        resultCard.setVisibility(View.GONE);
        recordingStatus.setVisibility(View.GONE);
        updateRecordingUI(false);

        // Delete recorded file
        if (audioFilePath != null) {
            new File(audioFilePath).delete();
        }
    }

    private void goToNextSentence() {
        // Go to next sentence (implementation depends on your data structure)
        Toast.makeText(this, "Moving to next sentence...", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền ghi âm đã được cấp", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Cần quyền ghi âm để sử dụng tính năng này", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cleanup resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (mediaRecorder != null) {
            mediaRecorder.release();
        }

        if (recordingTimerObj != null) {
            recordingTimerObj.cancel();
        }

        // Delete temporary audio file
        if (audioFilePath != null) {
            File audioFile = new File(audioFilePath);
            if (audioFile.exists()) {
                audioFile.delete();
            }
        }
    }
}