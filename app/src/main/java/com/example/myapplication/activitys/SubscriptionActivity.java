package com.example.myapplication.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.SubscriptionPlan;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        rvPlans = findViewById(R.id.rvPlans);
        btnPurchase = findViewById(R.id.btnPurchase);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnPurchase.setOnClickListener(v -> {
            if (selectedPlan == null) {
                Toast.makeText(this, "Please select a plan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Selected plan: " + selectedPlan.getName(), Toast.LENGTH_SHORT).show();
                // TODO: trigger payment
            }
        });

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getSubscriptionPlans().enqueue(new Callback<List<SubscriptionPlan>>() {
            @Override
            public void onResponse(Call<List<SubscriptionPlan>> call, Response<List<SubscriptionPlan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SubscriptionPlan> plans = response.body();
                    setupPlanAdapter(plans);
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionPlan>> call, Throwable t) {
                Toast.makeText(SubscriptionActivity.this, "Lỗi khi tải gói subscription", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupPlanAdapter(List<SubscriptionPlan> plans) {
        SubscriptionPlanAdapter adapter = new SubscriptionPlanAdapter(plans, this, plan -> {
            selectedPlan = plan;
        });
        rvPlans.setLayoutManager(new LinearLayoutManager(this));
        rvPlans.setAdapter(adapter);
    }
}

