package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.PartViewHolder> {

    private List<String> partsList;
    private OnPartClickListener onPartClickListener;

    public interface OnPartClickListener {
        void onPartClick(String part);
    }

    public PartAdapter(List<String> partsList, OnPartClickListener listener) {
        this.partsList = partsList;
        this.onPartClickListener = listener;
    }

    @NonNull
    @Override
    public PartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_part, parent, false);
        return new PartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartViewHolder holder, int position) {
        String part = partsList.get(position);
        holder.bind(part);
    }

    @Override
    public int getItemCount() {
        return partsList != null ? partsList.size() : 0;
    }

    class PartViewHolder extends RecyclerView.ViewHolder {
        private CardView partCard;
        private TextView partTitle;
        private TextView partDescription;

        public PartViewHolder(@NonNull View itemView) {
            super(itemView);
            partCard = itemView.findViewById(R.id.partCard);
            partTitle = itemView.findViewById(R.id.partTitle);
            partDescription = itemView.findViewById(R.id.partDescription);
        }

        public void bind(String part) {
            partTitle.setText("Part " + part);
            partDescription.setText("Practice pronunciation with sentences in part " + part);

            partCard.setOnClickListener(v -> {
                if (onPartClickListener != null) {
                    onPartClickListener.onPartClick(part);
                }
            });
        }
    }
}