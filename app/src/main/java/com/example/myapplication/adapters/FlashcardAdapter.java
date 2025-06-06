package com.example.myapplication.adapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Entitys.Flashcard;
import com.example.myapplication.R;

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

        private final FrameLayout cardFront;
        private final FrameLayout cardBack;
        private final TextView tvFront, tvBack;

        private boolean isFrontVisible = true;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFront = itemView.findViewById(R.id.card_front);
            cardBack = itemView.findViewById(R.id.card_back);
            tvFront = itemView.findViewById(R.id.tvFront);
            tvBack = itemView.findViewById(R.id.tvBack);

            itemView.setOnClickListener(v -> flipCard(v.getContext()));
        }

        public void bind(Flashcard flashcard) {
            tvFront.setText(flashcard.getWord());
            tvBack.setText(flashcard.getMeaning());

            cardFront.setVisibility(View.VISIBLE);
            cardBack.setVisibility(View.GONE);
            isFrontVisible = true;
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
