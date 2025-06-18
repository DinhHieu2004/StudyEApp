package com.example.myapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.SubscriptionPlan;
import com.example.myapplication.R;

import java.util.List;

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.PlanViewHolder> {

    private List<SubscriptionPlan> plans;
    private int selectedPosition = -1;
    private Context context;
    private OnPlanSelectedListener listener;

    public interface OnPlanSelectedListener {
        void onPlanSelected(SubscriptionPlan plan);
    }

    public SubscriptionPlanAdapter(List<SubscriptionPlan> plans, Context context, OnPlanSelectedListener listener) {
        this.plans = plans;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscription_card_item, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        SubscriptionPlan plan = plans.get(position);

        holder.title.setText(plan.getName());
        holder.price.setText(plan.getPrice() == 0 ? "Free" : String.format("%,.0f đ", plan.getPrice()));

        // Hiển thị các tính năng
        holder.featuresLayout.removeAllViews();
        for (String feature : plan.getFeatures()) {
            TextView tv = new TextView(context);
            tv.setText("✓ " + feature);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(14);
            tv.setPadding(0, 0, 0, 8);
            holder.featuresLayout.addView(tv);
        }

        // Xử lý chọn - Tick + đổi nền
        if (selectedPosition == position) {
            holder.ivCheck.setVisibility(View.VISIBLE);
            holder.cardContent.setBackgroundResource(R.drawable.card_selected_bg);
        } else {
            holder.ivCheck.setVisibility(View.GONE);
            holder.cardContent.setBackgroundResource(R.drawable.card_default_bg);
        }

        // Sự kiện click
        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            listener.onPlanSelected(plan);
        });
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        LinearLayout featuresLayout, cardContent;
        ImageView ivCheck;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.planTitle);
            price = itemView.findViewById(R.id.planPrice);
            featuresLayout = itemView.findViewById(R.id.featuresLayout);
            ivCheck = itemView.findViewById(R.id.ivSelectedIcon);
            cardContent = itemView.findViewById(R.id.cardContent);
        }
    }
}
