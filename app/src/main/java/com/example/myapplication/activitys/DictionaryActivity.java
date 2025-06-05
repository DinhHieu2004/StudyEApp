package com.example.myapplication.activitys;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.response.DictionaryResponse;
import com.example.myapplication.R;
import com.example.myapplication.adapters.MeaningAdapter;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class DictionaryActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnSearch;
    private TextView tvWord;
    private LinearLayout llPhonetics;
    private RecyclerView rvMeanings;
    private ProgressBar progressBar;

    private ApiService apiService;
    private MeaningAdapter meaningAdapter;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        tvWord = findViewById(R.id.tvWord);
        llPhonetics = findViewById(R.id.llPhonetics);
        rvMeanings = findViewById(R.id.rvMeanings);
        progressBar = findViewById(R.id.progressBar);

        apiService = ApiClient.getClient().create(ApiService.class);

        rvMeanings.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                searchWord(keyword);
            }
        });
    }

    private void searchWord(String word) {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getWord(word).enqueue(new Callback<DictionaryResponse>() {
            @Override
            public void onResponse(Call<DictionaryResponse> call, Response<DictionaryResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    showResult(response.body());
                } else {
                    Toast.makeText(DictionaryActivity.this, "Không tìm thấy từ", Toast.LENGTH_SHORT).show();
                    clearUI();
                }
            }

            @Override
            public void onFailure(Call<DictionaryResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DictionaryActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                clearUI();
            }
        });
    }

    private void showResult(DictionaryResponse dictionaryResponse) {
        String rawWord = dictionaryResponse.getWord();
        String decodedWord;
        try {
            decodedWord = URLDecoder.decode(rawWord, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            decodedWord = rawWord;
        }

        tvWord.setText(decodedWord);

        // Hiển thị phonetics
        llPhonetics.removeAllViews();
        if (dictionaryResponse.getPhonetics() != null) {
            for (DictionaryResponse.Phonetic phonetic : dictionaryResponse.getPhonetics()) {
                View item = getLayoutInflater().inflate(R.layout.item_phonetic, llPhonetics, false);
                TextView tvPhonetic = item.findViewById(R.id.tvPhonetic);
                ImageButton btnPlay = item.findViewById(R.id.btnPlayAudio);

                tvPhonetic.setText(phonetic.getText());

                btnPlay.setOnClickListener(v -> playAudio(phonetic.getAudio()));

                llPhonetics.addView(item);
            }
        }

        // Hiển thị meanings
        if (dictionaryResponse.getMeanings() != null) {
            meaningAdapter = new MeaningAdapter(this, dictionaryResponse.getMeanings());
            rvMeanings.setAdapter(meaningAdapter);
        }
    }

    private void playAudio(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Không có âm thanh phát", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearUI() {
        tvWord.setText("");
        llPhonetics.removeAllViews();
        if (meaningAdapter != null) {
            meaningAdapter = new MeaningAdapter(this, null);
            rvMeanings.setAdapter(meaningAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}



