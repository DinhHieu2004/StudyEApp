package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.response.DictionaryResponse;

import java.util.List;
import com.example.myapplication.R;

public class DefinitionAdapter extends RecyclerView.Adapter<DefinitionAdapter.DefinitionViewHolder> {

    private final List<DictionaryResponse.Definition> definitions;

    public DefinitionAdapter(List<DictionaryResponse.Definition> definitions) {
        this.definitions = definitions;
    }

    @NonNull
    @Override
    public DefinitionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_definition, parent, false);
        return new DefinitionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DefinitionViewHolder holder, int position) {
        DictionaryResponse.Definition def = definitions.get(position);

        holder.tvDefinition.setText(def.getDefinition());

        if (def.getVietnameseDefinition() != null && !def.getVietnameseDefinition().isEmpty()) {
            holder.tvVietnameseDefinition.setText(def.getVietnameseDefinition());
            holder.tvVietnameseDefinition.setVisibility(View.VISIBLE);
        } else {
            holder.tvVietnameseDefinition.setVisibility(View.GONE);
        }

        if (def.getExample() != null && !def.getExample().isEmpty()) {
            holder.tvExample.setText("\"" + def.getExample() + "\"");
            holder.tvExample.setVisibility(View.VISIBLE);
        } else {
            holder.tvExample.setVisibility(View.GONE);
        }

        if (def.getVietnameseExample() != null && !def.getVietnameseExample().isEmpty()) {
            holder.tvVietnameseExample.setText(def.getVietnameseExample());
            holder.tvVietnameseExample.setVisibility(View.VISIBLE);
        } else {
            holder.tvVietnameseExample.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return definitions != null ? definitions.size() : 0;
    }

    public static class DefinitionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDefinition, tvVietnameseDefinition, tvExample, tvVietnameseExample;

        public DefinitionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDefinition = itemView.findViewById(R.id.tvDefinition);
            tvVietnameseDefinition = itemView.findViewById(R.id.tvVietnameseDefinition);
            tvExample = itemView.findViewById(R.id.tvExample);
            tvVietnameseExample = itemView.findViewById(R.id.tvVietnameseExample);
        }
    }
}

