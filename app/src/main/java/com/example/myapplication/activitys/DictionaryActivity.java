package com.example.myapplication.activitys;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.response.DictionaryResponse;
import com.example.myapplication.Entitys.HistorySearchWord;
import com.example.myapplication.R;
import com.example.myapplication.adapters.MeaningAdapter;
import com.example.myapplication.fragments.SearchDictionaryFragment;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.example.myapplication.utils.AppDatabase;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class DictionaryActivity extends AppCompatActivity {

    private TextView tvWord;
    private LinearLayout llPhonetics;
    private RecyclerView rvMeanings;
    private ProgressBar progressBar;

    private ApiService apiService;
    private MeaningAdapter meaningAdapter;
    private MediaPlayer mediaPlayer;
    private DictionaryResponse currentResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_search_dictionary, new SearchDictionaryFragment())
                    .commit();
        }

        tvWord = findViewById(R.id.tvWord);
        llPhonetics = findViewById(R.id.llPhonetics);
        rvMeanings = findViewById(R.id.rvMeanings);
        progressBar = findViewById(R.id.progressBar);

        apiService = ApiClient.getClient().create(ApiService.class);

        rvMeanings.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            String json = savedInstanceState.getString("cached_response");
            if (json != null) {
                currentResponse = new Gson().fromJson(json, DictionaryResponse.class);
                showResult(currentResponse);
                return;
            }
        }

        String word = getIntent().getStringExtra("word");
        if (word != null && !word.isEmpty()) {
            searchWord(word);
        } else {
            tvWord.setText("Không có từ để tra");
            Toast.makeText(this, "Không có từ để tra", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchWord(String word) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(DictionaryActivity.this);
            HistorySearchWord history = db.historySearchWordDao().findByWord(word);

            if (history != null) {
                // Từ có trong db, parse JSON rồi hiển thị UI
                String jsonData = history.getJsonData();
                DictionaryResponse cachedResponse = new Gson().fromJson(jsonData, DictionaryResponse.class);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    showResult(cachedResponse);

                });
            } else {
                // Từ chưa có, gọi API
                runOnUiThread(() -> apiService.getWord(word).enqueue(new Callback<DictionaryResponse>() {
                    @Override
                    public void onResponse(Call<DictionaryResponse> call, Response<DictionaryResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            DictionaryResponse responseBody = response.body();
                            showResult(responseBody);

                            // Lưu vào database trong thread khác
                            new Thread(() -> {
                                String json = new Gson().toJson(responseBody);
                                HistorySearchWord newEntry = new HistorySearchWord(word, System.currentTimeMillis(), json);
                                db.historySearchWordDao().insert(newEntry);
                            }).start();

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
                }));
            }
        }).start();
    }


    private void showResult(DictionaryResponse dictionaryResponse) {
        currentResponse = dictionaryResponse;
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
                String audioUrl = phonetic.getAudio();
                if (audioUrl != null && !audioUrl.isEmpty()) {
                    View item = getLayoutInflater().inflate(R.layout.item_phonetic, llPhonetics, false);

                    TextView tvPhonetic = item.findViewById(R.id.tvPhonetic);
                    ImageButton btnPlay = item.findViewById(R.id.btnPlayAudio);

                    if (phonetic.getText() != null && !phonetic.getText().isEmpty()) {
                        tvPhonetic.setText(phonetic.getText());
                        tvPhonetic.setVisibility(View.VISIBLE);
                    } else {
                        tvPhonetic.setVisibility(View.GONE);
                    }

                    btnPlay.setOnClickListener(v -> playAudio(audioUrl));

                    llPhonetics.addView(item);
                }
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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (currentResponse != null) {
            String json = new Gson().toJson(currentResponse);
            outState.putString("cached_response", json);
        }
    }
}



