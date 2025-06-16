package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DTO.response.SentencePartResponse;
import com.example.myapplication.R;
import com.example.myapplication.activitys.PartListActivity;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PronounceFragment extends Fragment {

    private CardView cardA1;
    private CardView cardA2;
    private CardView cardB1;
    private CardView cardB2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pronounce, container, false);

        cardA1 = view.findViewById(R.id.cardA1);
        cardA2 = view.findViewById(R.id.cardA2);
        cardB1 = view.findViewById(R.id.cardB1);
        cardB2 = view.findViewById(R.id.cardB2);

        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        cardA1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedLevel("A1");
                fetchParts("A1");
            }
        });

        cardA2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedLevel("A2");

                fetchParts("A2");
            }
        });

        cardB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveSelectedLevel("B1");

                fetchParts("B1");
            }
        });

        cardB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedLevel("B2");

                fetchParts("B2");
            }
        });
    }

    private void fetchParts(String level) {
        // Kiểm tra xem Fragment đã attach vào Activity chưa và context có sẵn sàng không
        if (getContext() == null) {
            Log.e("PronounceFragment", "Context is null, cannot make API call.");
            return;
        }

        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        Call<SentencePartResponse> call = apiService.getPart(level);

        call.enqueue(new Callback<SentencePartResponse>() {
            @Override
            public void onResponse(Call<SentencePartResponse> call, Response<SentencePartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SentencePartResponse partResponse = response.body();

                    if (partResponse.getParts() != null && !partResponse.getParts().isEmpty()) {
                        Log.d("API_CALL", "Fetched parts for " + level + ": " + partResponse.getParts().toString());



                        // Chuyển sang PartListActivity và truyền dữ liệu
                        Intent intent = new Intent(requireContext(), PartListActivity.class);
                        intent.putExtra("level", level);
                        intent.putStringArrayListExtra("parts_list", new ArrayList<>(partResponse.getParts()));
                        startActivity(intent);

                    } else {
                        Toast.makeText(requireContext(),
                                "Không tìm thấy phần nào cho cấp độ " + level,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Xử lý lỗi từ server
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("API_CALL", "Lỗi khi phân tích lỗi phản hồi: " + e.getMessage());
                    }
                    Log.e("API_CALL", "Lỗi khi lấy phần cho " + level + ": " + response.code() + " - " + errorBody);
                    Toast.makeText(requireContext(),
                            "Không thể tải phần cho cấp độ " + level,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SentencePartResponse> call, Throwable t) {
                // Xử lý lỗi mạng
                Log.e("API_CALL", "Ngoại lệ khi tải phần cho " + level + ": " + t.getMessage(), t);
                Toast.makeText(requireContext(),
                        "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void saveSelectedLevel(String level) {
        requireContext()
                .getSharedPreferences("APP_PREFS", getContext().MODE_PRIVATE)
                .edit()
                .putString("SELECTED_LEVEL", level)
                .apply();
    }

}