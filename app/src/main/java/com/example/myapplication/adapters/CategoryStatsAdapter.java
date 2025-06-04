package com.example.myapplication.adapters;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activitys.StatisticsActivity;

import java.text.DecimalFormat;
import java.util.List;

public class CategoryStatsAdapter extends RecyclerView.Adapter<CategoryStatsAdapter.ViewHolder> {

    private List<StatisticsActivity.CategoryStats> categoryStatsList;
    private DecimalFormat decimalFormat = new DecimalFormat("#.#");

    public CategoryStatsAdapter(List<StatisticsActivity.CategoryStats> categoryStatsList) {
        this.categoryStatsList = categoryStatsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_stats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatisticsActivity.CategoryStats stats = categoryStatsList.get(position);

        holder.txtCategoryName.setText(stats.getCategoryName());
        holder.txtQuestionCount.setText(stats.getTotalQuestions() + " câu hỏi");
        holder.txtCorrectCount.setText(stats.getCorrectAnswers() + " đúng");
        holder.txtAccuracy.setText(decimalFormat.format(stats.getAccuracy()) + "%");

        // Thiết lập ProgressBar
        int progress = (int) stats.getAccuracy();
        holder.progressAccuracy.setProgress(progress);

        // Đổi màu dựa trên độ chính xác
        if (stats.getAccuracy() >= 80) {
            holder.txtAccuracy.setTextColor(Color.parseColor("#4CAF50"));
        } else if (stats.getAccuracy() >= 50) {
            holder.txtAccuracy.setTextColor(Color.parseColor("#FF9800"));
        } else {
            holder.txtAccuracy.setTextColor(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return categoryStatsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryName, txtQuestionCount, txtCorrectCount, txtAccuracy;
        ProgressBar progressAccuracy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            txtQuestionCount = itemView.findViewById(R.id.txtQuestionCount);
            txtCorrectCount = itemView.findViewById(R.id.txtCorrectCount);
            txtAccuracy = itemView.findViewById(R.id.txtAccuracy);
            progressAccuracy = itemView.findViewById(R.id.progressAccuracy);
        }
    }
}
