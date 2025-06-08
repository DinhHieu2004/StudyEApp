package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.response.DictionaryResponse;
import com.example.myapplication.R;

import java.util.List;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.ViewHolder> {

    private final Context context;
    private final List<DictionaryResponse.Meaning> meanings;

    public MeaningAdapter(Context context, List<DictionaryResponse.Meaning> meanings) {
        this.context = context;
        this.meanings = meanings;
    }

    @NonNull
    @Override
    public MeaningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meaning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeaningAdapter.ViewHolder holder, int position) {
        DictionaryResponse.Meaning meaning = meanings.get(position);
        holder.tvPartOfSpeech.setText(meaning.getPartOfSpeech());

        // Setup RecyclerView con cho definitions
        DefinitionAdapter definitionAdapter = new DefinitionAdapter(meaning.getDefinitions());
        holder.rvDefinitions.setLayoutManager(new LinearLayoutManager(context));
        holder.rvDefinitions.setAdapter(definitionAdapter);
    }

    @Override
    public int getItemCount() {
        return meanings != null ? meanings.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartOfSpeech;
        RecyclerView rvDefinitions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartOfSpeech = itemView.findViewById(R.id.tvPartOfSpeech);
            rvDefinitions = itemView.findViewById(R.id.rvDefinitions);
        }
    }
}
