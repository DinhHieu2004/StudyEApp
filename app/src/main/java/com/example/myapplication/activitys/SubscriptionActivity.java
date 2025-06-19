package com.example.myapplication.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.SubscriptionPlan;
import com.example.myapplication.DTO.response.UserResponse;
import com.example.myapplication.R;
import com.example.myapplication.adapters.SubscriptionPlanAdapter;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity {

    private RecyclerView rvPlans;
    private Button btnPurchase;
    private SubscriptionPlan selectedPlan;
    private List<SubscriptionPlan> plans;

    private SubscriptionPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        rvPlans = findViewById(R.id.rvPlans);
        btnPurchase = findViewById(R.id.btnPurchase);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnPurchase.setOnClickListener(v -> {
            if (selectedPlan == null) {
                Toast.makeText(this, "Vui lòng chọn một gói", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPlan.isSubscribed()) {
                Toast.makeText(this, "Gói này đã được đăng ký", Toast.LENGTH_SHORT).show();
                return;
            }

            long amount = (long) selectedPlan.getPrice();
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("amount", amount);
            startActivity(intent);
        });

        loadSubscriptionPlans();
        handlePaymentReturnIfAny();
    }

    private void loadSubscriptionPlans() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getSubscriptionPlans().enqueue(new Callback<List<SubscriptionPlan>>() {
            @Override
            public void onResponse(Call<List<SubscriptionPlan>> call, Response<List<SubscriptionPlan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    plans = response.body();
                    fetchUserSubscriptionStatus();
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionPlan>> call, Throwable t) {
                Toast.makeText(SubscriptionActivity.this, "Lỗi khi tải gói subscription", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserSubscriptionStatus() {
        String uid = getUidFromLocal(this);
        if (uid == null) return;

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getUserProfile(uid).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String subscribedPlanName = response.body().getSubscriptionPlan(); // hoặc getPlanId() nếu có ID

                    for (SubscriptionPlan plan : plans) {
                        if (plan.getName().equalsIgnoreCase(subscribedPlanName)) {
                            plan.setIsSubscribed(true);
                        } else {
                            plan.setIsSubscribed(false);
                        }
                    }

                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    prefs.edit().putString("subscribed_plan_id", subscribedPlanName).apply();

                    adapter = new SubscriptionPlanAdapter(plans, SubscriptionActivity.this, selected -> {
                        selectedPlan = selected;
                    });
                    rvPlans.setLayoutManager(new LinearLayoutManager(SubscriptionActivity.this));
                    rvPlans.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(SubscriptionActivity.this, "Không thể lấy trạng thái đăng ký", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePaymentReturnIfAny() {
        Uri data = getIntent().getData();
        if (data != null && "myapp".equals(data.getScheme()) && "payment_result".equals(data.getHost())) {
            new AlertDialog.Builder(this)
                    .setTitle("Thanh toán thành công")
                    .setMessage("Cảm ơn bạn đã mua gói!")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();

            String uid = getUidFromLocal(this);
            if (uid != null) {
                ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
                apiService.updateSubscription(uid, "super").enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("UPDATE", "Status: " + response.code());
                        Log.d("UPDATE", "Success: " + response.isSuccessful());
                        if (response.isSuccessful()) {
                            RecyclerView.Adapter adapter = rvPlans.getAdapter();
                            if (adapter instanceof SubscriptionPlanAdapter) {
                                List<SubscriptionPlan> plans = ((SubscriptionPlanAdapter) adapter).getPlans();
                                for (SubscriptionPlan plan : plans) {
                                    plan.setIsSubscribed(plan.getName().equalsIgnoreCase("super"));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("UPDATE", "Error", t);
                        Toast.makeText(SubscriptionActivity.this, "Không thể cập nhật gói", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void setupPlanAdapter(List<SubscriptionPlan> plans) {
        SubscriptionPlanAdapter adapter = new SubscriptionPlanAdapter(plans, this, plan -> {
            selectedPlan = plan;
        });
        rvPlans.setLayoutManager(new LinearLayoutManager(this));
        rvPlans.setAdapter(adapter);
    }

    public static String getUidFromLocal(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return prefs.getString("uid", null);
    }
}

