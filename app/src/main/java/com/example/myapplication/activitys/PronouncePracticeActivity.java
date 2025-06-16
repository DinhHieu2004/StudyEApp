package com.example.myapplication.activitys;

import static org.apache.commons.logging.Log.*;
import static java.lang.StrictMath.log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.example.myapplication.DTO.response.PartProgressDTO;
import com.example.myapplication.DTO.response.PhonemeResponse;
import com.example.myapplication.R;
import com.example.myapplication.model.PartProgress;
import com.example.myapplication.model.Sentence;
import com.example.myapplication.model.TextRequest;
import com.example.myapplication.model.UserProgress;
import com.example.myapplication.model.UserProgressForPronounce;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PronouncePracticeActivity extends AppCompatActivity {

    private static final Log log = LogFactory.getLog(PronouncePracticeActivity.class);
    private TextView practiceText;
    private boolean isShowingCompletionDialog = false;

    private TextView phoneticText;
    private TextView yourRecordingLabel;
    private TextView yourRecordingText;
    private TextView scoreText;
    private TextView statusBadge;
    private List<Sentence> sentenceList;
    private int currentIndex = 0;

    private TextToSpeech textToSpeech;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;
    private boolean isRecording = false;
    private LinearLayout listenButton, recordButton, playRecordedButton;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private int part;

    private String level;

    private ApiService apiService;
    private PartProgressDTO currentPartProgress;
    private TextView progressText; // Hiển thị % hoàn thành

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pronounce_practive);

        checkPermissions();

      //  part = getIntent().getIntExtra("part", 0);
        String partStr = getIntent().getStringExtra("part");

       // System.out.println("part" + partStr );

        assert partStr != null;
        part = Integer.parseInt(partStr);

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        level = prefs.getString("SELECTED_LEVEL", "");


        apiService = ApiClient.getClient(this).create(ApiService.class);


        loadPartProgress(part, level);

        loadIncompleteSentences(part, level);





        // Khởi tạo views
        practiceText = findViewById(R.id.practiceText);
        phoneticText = findViewById(R.id.phoneticText);
        yourRecordingLabel = findViewById(R.id.yourRecordingLabel);
        yourRecordingText = findViewById(R.id.yourRecordingText);
        scoreText = findViewById(R.id.scoreText);
        statusBadge = findViewById(R.id.statusBadge);
        listenButton = findViewById(R.id.listenButton);
        recordButton = findViewById(R.id.recordButton);
        progressText = findViewById(R.id.progressText); // Make sure this exists in your layout

        //   playRecordedButton = findViewById(R.id.playRecordedButton);

        // Lấy danh sách câu từ Intent
        sentenceList = (List<Sentence>) getIntent().getSerializableExtra("sentences");
        if (sentenceList != null && !sentenceList.isEmpty()) {
            showSentence(currentIndex);
        }

        // Khởi tạo TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Ngôn ngữ không được hỗ trợ", Toast.LENGTH_SHORT).show();
                    listenButton.setEnabled(false);
                }
            } else {
                Toast.makeText(this, "Khởi tạo TextToSpeech thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // Đường dẫn lưu file ghi âm
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/recorded_audio.3gp";

        // Khởi tạo SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        setupSpeechRecognizer();

        // Cấu hình Intent cho SpeechRecognizer
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        // Xử lý nút Listen
        listenButton.setOnClickListener(v -> {
            if (sentenceList != null && !sentenceList.isEmpty()) {
                String sentence = sentenceList.get(currentIndex).getContent();
                textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // Xử lý nút Record (chỉ nhận dạng giọng nói)
        recordButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                return;
            }
            yourRecordingText.setText("Đang nghe...");
            speechRecognizer.startListening(recognizerIntent);
        });

        // Xử lý nút Play Recorded Audio
//        playRecordedButton.setOnClickListener(v -> playRecordedAudio());

        // Xử lý nút Next
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (currentIndex < sentenceList.size() - 1) {
                currentIndex++;
                showSentence(currentIndex);
                resetRecordingUI();
            }
        });

        // Xử lý nút Try Again
        Button tryAgainButton = findViewById(R.id.tryAgainButton);
        tryAgainButton.setOnClickListener(v -> {
            showSentence(currentIndex);
            resetRecordingUI();
        });
    }

    private void loadPartProgress(int partId, String level) {
        Call<PartProgressDTO> call = apiService.getPartProgress(partId, level);
        call.enqueue(new Callback<PartProgressDTO>() {
            @Override
            public void onResponse(Call<PartProgressDTO> call, Response<PartProgressDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPartProgress = response.body();
                    updateProgressUI();
                } else {
                }
            }

            @Override
            public void onFailure(Call<PartProgressDTO> call, Throwable t) {
                Toast.makeText(PronouncePracticeActivity.this,
                        "Không thể tải tiến độ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressUI() {
        if (currentPartProgress != null) {
            String progressText = String.format("Tiến độ: %d/%d câu (%.1f%%)",
                    currentPartProgress.getCompletedSentences(),
                    currentPartProgress.getTotalSentences(),
                    currentPartProgress.getCompletionPercentage());
            this.progressText.setText(progressText);
        }
    }

    private void loadIncompleteSentences(int partId, String level) {
        Call<List<Sentence>> call = apiService.getIncompleteSentences(partId, level);
        call.enqueue(new Callback<List<Sentence>>() {
            @Override
            public void onResponse(Call<List<Sentence>> call, Response<List<Sentence>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sentence> incompleteSentences = response.body();
                    if (!incompleteSentences.isEmpty()) {
                        sentenceList = incompleteSentences;
                        currentIndex = 0;
                        showSentence(currentIndex);
                    } else {
                        Toast.makeText(PronouncePracticeActivity.this,
                                "Bạn đã hoàn thành tất cả câu trong part này!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    Toast.makeText(PronouncePracticeActivity.this,
                            "Lỗi tải danh sách câu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Sentence>> call, Throwable t) {
                Toast.makeText(PronouncePracticeActivity.this,
                        "Không thể tải danh sách câu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                showPermissionExplanationDialog();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void showPermissionExplanationDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Yêu cầu quyền Microphone");
        builder.setMessage("Ứng dụng cần quyền truy cập microphone để nhận dạng giọng nói. Vui lòng cấp quyền để tiếp tục.");
        builder.setPositiveButton("Cấp quyền", (dialog, which) -> {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            Toast.makeText(this, "Không thể sử dụng tính năng luyện phát âm mà không có quyền microphone", Toast.LENGTH_LONG).show();
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền! Bạn có thể sử dụng tính năng ghi âm.", Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    showPermissionDeniedDialog();
                } else {
                    showSettingsDialog();
                }
            }
        }
    }

    private void showPermissionDeniedDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Yêu cầu quyền");
        builder.setMessage("Quyền microphone là cần thiết cho luyện phát âm. Bạn có muốn cấp quyền ngay bây giờ?");
        builder.setPositiveButton("Thử lại", (dialog, which) -> {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
        });
        builder.setNegativeButton("Thoát", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    private void showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Yêu cầu quyền");
        builder.setMessage("Quyền microphone bị từ chối. Vui lòng bật quyền này trong Cài đặt > Ứng dụng > MyApplication > Quyền.");
        builder.setPositiveButton("Mở Cài đặt", (dialog, which) -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 100);
        });
        builder.setNegativeButton("Thoát", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền! Bạn có thể sử dụng tính năng ghi âm.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền vẫn bị từ chối. Ứng dụng sẽ thoát.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void showSentence(int index) {
        Sentence s = sentenceList.get(index);
        practiceText.setText(s.getContent());
        fetchPhonemes(s.getContent());

        // Hiển thị các thành phần UI ngay từ đầu
        yourRecordingLabel.setVisibility(View.VISIBLE);
        yourRecordingText.setVisibility(View.VISIBLE);
        yourRecordingText.setText("Chưa ghi âm");

        scoreText.setVisibility(View.VISIBLE);
        scoreText.setText("0%");

        statusBadge.setVisibility(View.VISIBLE);
        statusBadge.setText("Chưa thực hiện");
        statusBadge.setTextColor(Color.parseColor("#9E9E9E"));
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Quyền ghi âm chưa được cấp", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            LinearLayout recordingStatus = findViewById(R.id.recordingStatus);
            recordingStatus.setVisibility(View.VISIBLE);

            TextView recordText = findViewById(R.id.recordText);
            recordText.setText("Dừng");

        } catch (IOException e) {
            e.printStackTrace();
            // Toast.makeText(this, "Ghi âm thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            isRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
            //  Toast.makeText(this, "Lỗi ghi âm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            isRecording = false;
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            LinearLayout recordingStatus = findViewById(R.id.recordingStatus);
            recordingStatus.setVisibility(View.GONE);

            TextView recordText = findViewById(R.id.recordText);
            recordText.setText("Ghi âm");

            // Toast.makeText(this, "Đã lưu ghi âm", Toast.LENGTH_SHORT).show();
        }
    }

    private void playRecordedAudio() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Đang phát ghi âm", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Phát lại thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPhonemes(String text) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dd76-34-169-238-205.ngrok-free.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService api = retrofit.create(ApiService.class);
        TextRequest request = new TextRequest(text);

        Call<PhonemeResponse> call = api.getPhonemes(request);
        call.enqueue(new Callback<PhonemeResponse>() {
            @Override
            public void onResponse(Call<PhonemeResponse> call, Response<PhonemeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String phonemes = response.body().getPhonemes();
                    runOnUiThread(() -> phoneticText.setText(phonemes));
                } else {
                    Toast.makeText(PronouncePracticeActivity.this, "Không lấy được phonemes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PhonemeResponse> call, Throwable t) {
                Toast.makeText(PronouncePracticeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                //   Toast.makeText(PronouncePracticeActivity.this, "Sẵn sàng nhận giọng nói", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                //    Toast.makeText(PronouncePracticeActivity.this, "Đã phát hiện giọng nói", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                String errorMessage;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        errorMessage = "Lỗi ghi âm";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        errorMessage = "Lỗi phía client";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        errorMessage = "Không đủ quyền";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        errorMessage = "Lỗi mạng";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        errorMessage = "Hết thời gian kết nối mạng";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage = "Không tìm thấy kết quả phù hợp";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        errorMessage = "Dịch vụ nhận dạng đang bận";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        errorMessage = "Lỗi server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        errorMessage = "Không có đầu vào giọng nói";
                        break;
                    default:
                        errorMessage = "Lỗi không xác định: " + error;
                        break;
                }
                //  Toast.makeText(PronouncePracticeActivity.this, "Lỗi nhận dạng giọng nói: " + errorMessage, Toast.LENGTH_LONG).show();
                resetRecordingUI();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);

                    // Hiển thị điểm số và trạng thái
                    String sampleText = practiceText.getText().toString();
                    double similarity = calculateSimilarity(recognizedText, sampleText);
                    int percent = (int) (similarity * 100);

                    scoreText.setVisibility(View.VISIBLE);
                    scoreText.setText(percent + "%");

                    statusBadge.setVisibility(View.VISIBLE);
                    if (similarity >= 0.9) {
                        String status = similarity >= 0.9 ? "Tuyệt vời!" : "Thử lại";
                        statusBadge.setText(status);
                        statusBadge.setText("Tuyệt vời!");
                        statusBadge.setTextColor(Color.parseColor("#4CAF50"));

                        savePracticeResult(recognizedText, percent, status);

                    } else if (similarity >= 0.7) {
                        statusBadge.setText("Khá tốt!");
                        statusBadge.setTextColor(Color.parseColor("#FF9800"));
                        savePracticeResult(recognizedText, percent, "Khá tốt!");
                    }

                    else {
                        statusBadge.setText("Thử lại");
                        statusBadge.setTextColor(Color.parseColor("#FF5722"));
                    }

                    // Hiển thị chuỗi đã ghi âm
                    yourRecordingLabel.setVisibility(View.VISIBLE);
                    yourRecordingText.setVisibility(View.VISIBLE);
                    yourRecordingText.setText(recognizedText);
                } else {
                    yourRecordingText.setText("Không nhận diện được giọng nói");
                    yourRecordingLabel.setVisibility(View.VISIBLE);
                    yourRecordingText.setVisibility(View.VISIBLE);
                }
            }

            private void savePracticeResult(String userSpeech, int score, String status) {

                System.out.println("save function");

                System.out.println("part" +part + "userSpeech "+ userSpeech + "score" + " status" +status );

                if (apiService == null) {
                    Toast.makeText(PronouncePracticeActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
                    return;
                }
                Sentence currentSentence = sentenceList.get(currentIndex);

                UserProgressForPronounce progress = new UserProgressForPronounce();
                progress.setPartId(part);
                //progress.setSentenceId(currentSentence.getId());
               // progress.setSentenceContent(currentSentence.getContent());
                progress.setSentence(currentSentence);

                progress.setUserSpeech(userSpeech);
                progress.setScore(score);
                progress.setStatus(status);
                progress.setCompleted(score >= 70);
                progress.setTimestamp(System.currentTimeMillis());

                Call<ResponseBody> call = apiService.savePracticeResult(progress);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                          //  Toast.makeText(PronouncePracticeActivity.this, "Đã lưu kết quả!", Toast.LENGTH_SHORT).show();
                            updatePartProgressOnServer();
                         //   moveToNextSentence();
                        } else {
                          //  Toast.makeText(PronouncePracticeActivity.this, "Lỗi lưu kết quả: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                      //  Toast.makeText(PronouncePracticeActivity.this, "Lỗi lưu kết quả: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void updatePartProgressOnServer() {
                Map<String, Object> progressData = new HashMap<>();
                progressData.put("part", part);
                progressData.put("level", level);

                Call<PartProgressDTO> call = apiService.updatePartProgress(progressData);
                call.enqueue(new Callback<PartProgressDTO>() {
                    @Override
                    public void onResponse(Call<PartProgressDTO> call, Response<PartProgressDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            currentPartProgress = response.body();
                            updateProgressUI();

                            // Kiểm tra hoàn thành part
                            if (currentPartProgress.getCompletionPercentage() >= 100) {
                                showPartCompletionDialog();
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<PartProgressDTO> call, Throwable t) {
                    }
                });
            }

            private void moveToNextSentence() {
                if (currentIndex < sentenceList.size() - 1) {
                    currentIndex++;
                    showSentence(currentIndex);
                    resetRecordingUI();
                } else {
                    // Hết câu trong part hiện tại
                    showPartCompletionDialog();
                }
            }



            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }
    private void showPartCompletionDialog() {
        if (isShowingCompletionDialog) return;

        isShowingCompletionDialog = true;

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Chúc mừng!");
        builder.setMessage(String.format("Bạn đã hoàn thành %.1f%% part này!",
                currentPartProgress.getCompletionPercentage()));
        builder.setPositiveButton("Tiếp tục", (dialog, which) -> {
            isShowingCompletionDialog = false;
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }


    private void resetRecordingUI() {
        // Không ẩn các element nữa, chỉ reset nội dung
        yourRecordingText.setText("Chưa ghi âm");
        scoreText.setText("0%");
        statusBadge.setText("Chưa thực hiện");
        statusBadge.setTextColor(Color.parseColor("#9E9E9E"));

        LinearLayout recordingStatus = findViewById(R.id.recordingStatus);
        recordingStatus.setVisibility(View.GONE);

        TextView recordText = findViewById(R.id.recordText);
        recordText.setText("Ghi âm");
    }

    private int calculateLevenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    private double calculateSimilarity(String input, String target) {
        input = input.toLowerCase().trim();
        target = target.toLowerCase().trim();
        int distance = calculateLevenshteinDistance(input, target);
        int maxLength = Math.max(input.length(), target.length());
        return maxLength == 0 ? 1.0 : 1.0 - ((double) distance / maxLength);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}