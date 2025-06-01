package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.DTO.response.LessionResponse;
import com.example.myapplication.R;
import com.example.myapplication.activitys.LessionDetailActivity;

import java.util.List;

public class LessionAdapter extends RecyclerView.Adapter<LessionAdapter.LessionViewHolder> {

    private final List<LessionResponse> lessionList;
    private final Context context;

    public LessionAdapter(Context context, List<LessionResponse> lessionList) {
        this.context = context;
        this.lessionList = lessionList;
    }

    @NonNull
    @Override
    public LessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lession, parent, false);
        return new LessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessionViewHolder holder, int position) {
        LessionResponse lession = lessionList.get(position);

        holder.titleText.setText(lession.getTitle());
        holder.descText.setText(lession.getDescription());
        holder.tagText.setText(lession.getTopicName());

        // Load ảnh nếu có URL
        if (lession.getImageUrl() != null && !lession.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(lession.getImageUrl())
                    .placeholder(R.drawable.placeholder_image) // placeholder tạm nếu không có ảnh
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image);
        }

        // click để mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LessionDetailActivity.class);
            intent.putExtra("lessionId", lession.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lessionList.size();
    }

    public static class LessionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText, descText, tagText;

        public LessionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleText = itemView.findViewById(R.id.titleText);
            descText = itemView.findViewById(R.id.descText);
            tagText = itemView.findViewById(R.id.tagText);
        }
    }
}
