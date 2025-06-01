package com.example.myapplication.adapters;

import android.content.Context;
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

import java.util.List;

public class SceneLearnAdapter extends RecyclerView.Adapter<SceneLearnAdapter.SceneViewHolder> {

    private final Context context;
    private final List<LessionResponse> sceneList;

    public SceneLearnAdapter(Context context, List<LessionResponse> sceneList) {
        this.context = context;
        this.sceneList = sceneList;
    }

    @NonNull
    @Override
    public SceneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scene_learn, parent, false);
        return new SceneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SceneViewHolder holder, int position) {
        LessionResponse scene = sceneList.get(position);

        holder.title.setText(scene.getTitle());
        holder.description.setText(scene.getDescription());
        holder.tag.setText(scene.getLevel());

        Glide.with(context)
                .load(scene.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return sceneList.size();
    }

    static class SceneViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description, tag;

        public SceneViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageScene);
            title = itemView.findViewById(R.id.titleScene);
            description = itemView.findViewById(R.id.descScene);
            tag = itemView.findViewById(R.id.tagScene);
        }
    }
}
