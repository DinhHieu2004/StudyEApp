package com.example.myapplication.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.DTO.Definition;
import com.example.myapplication.DTO.Meaning;
import com.example.myapplication.DTO.Phonetic;
import com.example.myapplication.DTO.response.FreeDictionaryResponse;
import com.example.myapplication.DTO.response.UnsplashResponse;
import com.example.myapplication.DTO.response.VocabularyResponse;
import com.example.myapplication.R;
import com.example.myapplication.services.FreeDictionaryApiService;
import com.example.myapplication.services.UnsplashApiService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabViewHolder> {
    private final Map<String, String> audioCache = new HashMap<>();

    private final Map<String, String> imageCache = new HashMap<>();

    private final Context context;
    private final List<VocabularyResponse> vocabList;

    public VocabularyAdapter(Context context, List<VocabularyResponse> vocabList) {
        this.context = context;
        this.vocabList = vocabList;
    }

    @NonNull
    @Override
    public VocabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vocabulary, parent, false);
        return new VocabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabViewHolder holder, int position) {
        VocabularyResponse vocab = vocabList.get(position);

        holder.layoutExtraInfo.setVisibility(View.GONE);
        holder.btnViewDetail.setImageResource(R.drawable.read_more);

        holder.textWord.setText(vocab.getWord());
        holder.textMeaning.setText("(n): " + vocab.getMeaning());
        holder.textExample.setText("→ " + vocab.getExample());

        // Xử lý nút phát âm (chỉ khi có audioUrl)
        holder.btnPlayAudioVoca.setOnClickListener(v -> {
            fetchAudioFromApi(vocab.getWord(), holder.btnPlayAudioVoca);
        });

        // Xử lý bookmark (hiện tại: demo, không lưu)
        holder.btnBookmark.setOnClickListener(v -> {
            Toast.makeText(context, "Đã đánh dấu: " + vocab.getWord(), Toast.LENGTH_SHORT).show();
        });

        // Xử lý toggle dropdown xem chi tiết
        holder.btnViewDetail.setOnClickListener(v -> {
            boolean isVisible = holder.layoutExtraInfo.getVisibility() == View.VISIBLE;
            holder.layoutExtraInfo.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            holder.btnViewDetail.setImageResource(isVisible ? R.drawable.read_more : R.drawable.expand_less);

            if (!isVisible) {
                holder.imageWord.setVisibility(View.VISIBLE);
                fetchExtraWordInfo(holder, vocab.getWord());
                fetchImageFromApi(vocab.getWord(), holder.imageWord);
            }
        });



    }

    private void fetchAudioFromApi(String word, ImageView btnPlayAudioVoca) {
        if (audioCache.containsKey(word)) {
            playAudio(audioCache.get(word), btnPlayAudioVoca);
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dictionaryapi.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FreeDictionaryApiService api = retrofit.create(FreeDictionaryApiService.class);
        api.getWordDetails(word).enqueue(new Callback<List<FreeDictionaryResponse>>() {
            @Override
            public void onResponse(Call<List<FreeDictionaryResponse>> call, Response<List<FreeDictionaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Phonetic> phonetics = response.body().get(0).getPhonetics();
                    for (Phonetic p : phonetics) {
                        if (p.getAudio() != null && !p.getAudio().isEmpty()) {
                            audioCache.put(word, p.getAudio());
                            playAudio(p.getAudio(), btnPlayAudioVoca);
                            return;
                        }
                    }
                }
                Toast.makeText(context, "Không có audio", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<FreeDictionaryResponse>> call, Throwable t) {
                Toast.makeText(context, "Lỗi khi tải audio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playAudio(String url, ImageView iconView) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            iconView.setColorFilter(ContextCompat.getColor(context, R.color.teal_100), PorterDuff.Mode.SRC_IN);

            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(mp -> mp.start());

            mediaPlayer.setOnCompletionListener(mp -> {
                iconView.clearColorFilter();
                mp.release();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                iconView.clearColorFilter();
                return false;
            });

            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Toast.makeText(context, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
            iconView.clearColorFilter();
        }
    }

    private void fetchExtraWordInfo(VocabViewHolder holder, String word) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dictionaryapi.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FreeDictionaryApiService api = retrofit.create(FreeDictionaryApiService.class);
        api.getWordDetails(word).enqueue(new Callback<List<FreeDictionaryResponse>>() {
            @Override
            public void onResponse(Call<List<FreeDictionaryResponse>> call, Response<List<FreeDictionaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    FreeDictionaryResponse data = response.body().get(0);

                    // IPA
                    if (data.getPhonetics() != null && !data.getPhonetics().isEmpty()) {
                        holder.textIPA.setText("IPA: " + data.getPhonetics().get(0).getText());
                    }

                    // Meaning
                    if (data.getMeanings() != null && !data.getMeanings().isEmpty()) {
                        Meaning meaning = data.getMeanings().get(0);
                        holder.textPartOfSpeech.setText("Part of speech: " + meaning.getPartOfSpeech());

                        if (meaning.getDefinitions() != null && !meaning.getDefinitions().isEmpty()) {
                            Definition def = meaning.getDefinitions().get(0);
                            holder.textDefinition.setText("Meaning: " + def.getDefinition());
                            if (def.getExample() != null) {
                                holder.textExtraExample.setText("Example: " + def.getExample());
                            } else {
                                holder.textExtraExample.setText("");
                            }
                        }
                    }
                } else {
                    holder.textDefinition.setText("No definition found.");
                }
            }

            @Override
            public void onFailure(Call<List<FreeDictionaryResponse>> call, Throwable t) {
                holder.textDefinition.setText("Failed to fetch extra info.");
                Log.e("FreeDictAPI", "Fetch error", t);
            }
        });
    }

    private void fetchImageFromApi(String word, ImageView imageView) {
        if (imageCache.containsKey(word)) {
            String cachedUrl = imageCache.get(word);
            Glide.with(context)
                    .load(cachedUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageView);
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.unsplash.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UnsplashApiService apiService = retrofit.create(UnsplashApiService.class);
        apiService.searchPhotos(word, "g2EUrogosDjSWoEw32gRhS4QWmP_PGEIyr35W7-hIHE")
                .enqueue(new Callback<UnsplashResponse>() {
                    @Override
                    public void onResponse(Call<UnsplashResponse> call, Response<UnsplashResponse> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().getResults().isEmpty()) {
                            String imageUrl = response.body().getResults().get(0).getUrls().getSmall();
                            imageCache.put(word, imageUrl);

                            Glide.with(context)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.placeholder_image)
                                    .into(imageView);
                        } else {
                            imageView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<UnsplashResponse> call, Throwable t) {
                        imageView.setVisibility(View.GONE);
                    }
                });
    }


    @Override
    public int getItemCount() {
        return vocabList.size();
    }

    public static class VocabViewHolder extends RecyclerView.ViewHolder {
        TextView textWord, textMeaning, textExample;
        TextView textIPA, textPartOfSpeech, textDefinition, textExtraExample;
        ImageView btnPlayAudioVoca, btnBookmark, btnViewDetail, imageWord;
        LinearLayout layoutExtraInfo;

        public VocabViewHolder(@NonNull View itemView) {
            super(itemView);

            textWord = itemView.findViewById(R.id.textWord);
            textMeaning = itemView.findViewById(R.id.textMeaning);
            textExample = itemView.findViewById(R.id.textExample);
            btnPlayAudioVoca = itemView.findViewById(R.id.btnPlayAudioVoca);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);

            // Các view mở rộng
            textIPA = itemView.findViewById(R.id.textIPA);
            textPartOfSpeech = itemView.findViewById(R.id.textPartOfSpeech);
            textDefinition = itemView.findViewById(R.id.textDefinition);
            textExtraExample = itemView.findViewById(R.id.textExtraExample);
            layoutExtraInfo = itemView.findViewById(R.id.layoutExtraInfo);
            imageWord = itemView.findViewById(R.id.imageWord);
        }
    }
}
