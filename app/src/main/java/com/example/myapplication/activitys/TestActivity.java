package com.example.myapplication.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView tvSample, tvResult;
    private Button btnSpeak, btnListen;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;

    private final int RECORD_AUDIO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        // Ánh xạ view
        tvSample = findViewById(R.id.tvSample);
        tvResult = findViewById(R.id.tvResult);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnListen = findViewById(R.id.btnListen);

        // Yêu cầu quyền ghi âm nếu chưa có
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_REQUEST_CODE);
        }

        // Khởi tạo TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int lang = tts.setLanguage(Locale.US);
                if (lang == TextToSpeech.LANG_MISSING_DATA ||
                        lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    btnSpeak.setEnabled(false);
                }
            }
        });

        // Phát âm câu mẫu
        btnSpeak.setOnClickListener(v -> {
            String text = tvSample.getText().toString();
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        });

        // Khởi tạo speechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) {
                tvResult.setText("Lỗi khi nhận giọng nói: " + error);
            }

            @Override public void onResults(Bundle results) {
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    tvResult.setText("Bạn nói: " + matches.get(0));
                }
            }

            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });

        // Intent không hiện popup
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        // Bắt đầu nghe
        btnListen.setOnClickListener(v -> {
            tvResult.setText("Đang nghe...");
            speechRecognizer.startListening(recognizerIntent);
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    // Xử lý kết quả xin quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                btnListen.setEnabled(false);
                tvResult.setText("Không có quyền ghi âm");
            }
        }
    }
}
