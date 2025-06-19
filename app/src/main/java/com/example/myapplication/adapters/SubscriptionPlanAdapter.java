package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.SubscriptionPlan;
import com.example.myapplication.R;
import com.example.myapplication.activitys.PaymentActivity;

import java.util.List;

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.PlanViewHolder> {

    private final List<SubscriptionPlan> plans;
    private int selectedPosition = -1;
    private final Context context;
    private final OnPlanSelectedListener listener;

    public List<SubscriptionPlan> getPlans() {
        return plans;
    }

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

        holder.featuresLayout.removeAllViews();
        for (String feature : plan.getFeatures()) {
            TextView tv = new TextView(context);
            tv.setText("✓ " + feature);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(14);
            tv.setPadding(0, 0, 0, 8);
            holder.featuresLayout.addView(tv);
        }

        boolean isSelected = selectedPosition == position;
        boolean isSubscribed = plan.isSubscribed();
        boolean isFreePlan = plan.getName().equalsIgnoreCase("free");

        if (isSubscribed && !isFreePlan) {
            // Gói trả phí đã đăng ký
            holder.cardContent.setBackgroundResource(R.drawable.card_paid_bg);
            holder.subscribedLabel.setVisibility(View.VISIBLE);
            holder.ivCheck.setVisibility(View.VISIBLE);

            TextView tvSubscribed = new TextView(context);
            tvSubscribed.setText("Đã đăng ký");
            tvSubscribed.setTextColor(Color.BLUE);
            tvSubscribed.setTypeface(null, Typeface.BOLD);
            holder.featuresLayout.addView(tvSubscribed);

            holder.itemView.setOnClickListener(null);
        } else {
            // Gói Free hoặc chưa đăng ký
            holder.cardContent.setBackgroundResource(isSelected
                    ? R.drawable.card_selected_bg
                    : R.drawable.card_default_bg);
            holder.subscribedLabel.setVisibility(View.GONE);
            holder.ivCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                int previous = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previous);
                notifyItemChanged(selectedPosition);
                listener.onPlanSelected(plan);
            });
        }
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, subscribedLabel;
        LinearLayout featuresLayout, cardContent;
        ImageView ivCheck;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.planTitle);
            price = itemView.findViewById(R.id.planPrice);
            featuresLayout = itemView.findViewById(R.id.featuresLayout);
            ivCheck = itemView.findViewById(R.id.ivSelectedIcon);
            cardContent = itemView.findViewById(R.id.cardContent);
            subscribedLabel = itemView.findViewById(R.id.planSubscribedLabel);
        }
    }
}

