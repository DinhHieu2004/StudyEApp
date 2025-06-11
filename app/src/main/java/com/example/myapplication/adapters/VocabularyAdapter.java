package com.example.myapplication.adapters;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.Definition;
import com.example.myapplication.DTO.Meaning;
import com.example.myapplication.DTO.response.FreeDictionaryResponse;
import com.example.myapplication.DTO.response.VocabularyResponse;
import com.example.myapplication.R;
import com.example.myapplication.services.FreeDictionaryApiService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabViewHolder> {

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

        holder.textWord.setText(vocab.getWord());
        holder.textMeaning.setText("(n): " + vocab.getMeaning());
        holder.textExample.setText("→ " + vocab.getExample());

        // Xử lý nút phát âm (chỉ khi có audioUrl)
        holder.btnPlayAudio.setOnClickListener(v -> {
            if (vocab.getAudioUrl() != null && !vocab.getAudioUrl().isEmpty()) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(vocab.getAudioUrl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    Toast.makeText(context, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Không có file âm thanh", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý bookmark (hiện tại: demo, không lưu)
        holder.btnBookmark.setOnClickListener(v -> {
            Toast.makeText(context, "Đã đánh dấu: " + vocab.getWord(), Toast.LENGTH_SHORT).show();
        });

        // Xử lý toggle dropdown xem chi tiết
        holder.btnViewDetail.setOnClickListener(v -> {
            if (holder.layoutExtraInfo.getVisibility() == View.GONE) {
                holder.layoutExtraInfo.setVisibility(View.VISIBLE);
                fetchExtraWordInfo(holder, vocab.getWord());
            } else {
                holder.layoutExtraInfo.setVisibility(View.GONE);
            }
        });

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
                                holder.textExtraExample.setText(""); // clear if no example
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

    @Override
    public int getItemCount() {
        return vocabList.size();
    }

    public static class VocabViewHolder extends RecyclerView.ViewHolder {
        TextView textWord, textMeaning, textExample;
        TextView textIPA, textPartOfSpeech, textDefinition, textExtraExample;
        ImageView btnPlayAudio, btnBookmark, btnViewDetail;
        LinearLayout layoutExtraInfo;

        public VocabViewHolder(@NonNull View itemView) {
            super(itemView);
            textWord = itemView.findViewById(R.id.textWord);
            textMeaning = itemView.findViewById(R.id.textMeaning);
            textExample = itemView.findViewById(R.id.textExample);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);

            // Các view mở rộng
            textIPA = itemView.findViewById(R.id.textIPA);
            textPartOfSpeech = itemView.findViewById(R.id.textPartOfSpeech);
            textDefinition = itemView.findViewById(R.id.textDefinition);
            textExtraExample = itemView.findViewById(R.id.textExtraExample);
            layoutExtraInfo = itemView.findViewById(R.id.layoutExtraInfo);
        }
    }
}
