package com.example.myapplication.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Entitys.Flashcard;
import com.example.myapplication.R;

import java.io.IOException;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private final List<Flashcard> flashcards;

    public FlashcardAdapter(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        holder.bind(flashcards.get(position));
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout cardFront, cardBack;
        private final TextView tvWord, tvPhonetic, tvMeaning, tvExample, tvExampleMeaning;
        private final ImageView imgWord;
        private final ImageButton btnVolume;

        private boolean isFrontVisible = true;
        private MediaPlayer mediaPlayer;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFront = itemView.findViewById(R.id.card_front);
            cardBack = itemView.findViewById(R.id.card_back);

            tvWord = itemView.findViewById(R.id.tvWord);
            tvPhonetic = itemView.findViewById(R.id.tvPhonetic);
            imgWord = itemView.findViewById(R.id.imgWord);
            btnVolume = itemView.findViewById(R.id.btnVolume);

            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            tvExample = itemView.findViewById(R.id.tvExample);
            tvExampleMeaning = itemView.findViewById(R.id.tvExampleMeaning);

            itemView.setOnClickListener(v -> flipCard(v.getContext()));
            btnVolume.setOnClickListener(v -> {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
        }

        public void bind(Flashcard flashcard) {
            tvWord.setText(flashcard.getWord());
            tvPhonetic.setText(flashcard.getPhonetic());
            tvMeaning.setText(flashcard.getMeaning());
            tvExample.setText(flashcard.getExample());
            tvExampleMeaning.setText(flashcard.getExampleMeaning());

            if (flashcard.getImageUrl() != null && !flashcard.getImageUrl().isEmpty()) {
                Glide.with(imgWord.getContext())
                        .load(flashcard.getImageUrl())
                        .placeholder(R.drawable.lession1)
                        .error(R.drawable.lession1)
                        .into(imgWord);
            } else {
                imgWord.setImageResource(R.drawable.lession1);
            }

            cardFront.setVisibility(View.VISIBLE);
            cardBack.setVisibility(View.GONE);
            isFrontVisible = true;

            btnVolume.setOnClickListener(v -> playSound(v.getContext(), flashcard.getAudioUrl()));
        }
        private void playSound(Context context, String audioUrl) {
            if (audioUrl == null || audioUrl.isEmpty()) {
                Toast.makeText(context, "Không có âm thanh", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.setOnPreparedListener(mp -> mp.start());
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    mediaPlayer = null;
                });
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Toast.makeText(context, "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
                    mp.release();
                    mediaPlayer = null;
                    return true; // đã xử lý lỗi
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                Toast.makeText(context, "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


        private void flipCard(Context context) {
            AnimatorSet flipOutAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_out);
            AnimatorSet flipInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.flip_in);

            if (isFrontVisible) {
                flipOutAnimator.setTarget(cardFront);
                flipInAnimator.setTarget(cardBack);

                flipOutAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cardFront.setVisibility(View.GONE);
                        cardBack.setVisibility(View.VISIBLE);
                        flipInAnimator.start();
                    }
                });

                flipOutAnimator.start();
            } else {
                flipOutAnimator.setTarget(cardBack);
                flipInAnimator.setTarget(cardFront);

                flipOutAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cardBack.setVisibility(View.GONE);
                        cardFront.setVisibility(View.VISIBLE);
                        flipInAnimator.start();
                    }
                });

                flipOutAnimator.start();
            }

            isFrontVisible = !isFrontVisible;
        }

    }
}
