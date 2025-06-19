package com.example.myapplication.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DTO.request.PaymentRequest;
import com.example.myapplication.DTO.response.PaymentResponse;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long amount = getIntent().getLongExtra("amount", 0);

        if (amount == 0) {
            Toast.makeText(this, "Gói này miễn phí, không cần thanh toán", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Retrofit retrofit = ApiClient.getClient(this);
        ApiService paymentApi = retrofit.create(ApiService.class);
        PaymentRequest request = new PaymentRequest(amount);

        paymentApi.createPayment(request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body().getPaymentUrl();

                    // Mở link VNPAY trong trình duyệt
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Lỗi tạo thanh toán (response)", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Gọi API thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
