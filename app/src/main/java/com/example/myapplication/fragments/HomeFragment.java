package com.example.myapplication.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DTO.response.LessionResponse;
import com.example.myapplication.DTO.response.PageResponse;
import com.example.myapplication.R;
import com.example.myapplication.adapters.LessionAdapter;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.services.LessionApiService;
import com.example.myapplication.utils.ApiClient;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private LessionAdapter adapter;
    private List<LessionResponse> lessionList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 cột
        adapter = new LessionAdapter(requireContext(), lessionList);
        recyclerView.setAdapter(adapter);

        loadLessions();
    }

    private void loadLessions() {
        LessionApiService api = ApiClient.getClient().create(LessionApiService.class);
        Call<PageResponse<LessionResponse>> call = api.getLessions(1L, 0, 4);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PageResponse<LessionResponse>> call, Response<PageResponse<LessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LessionResponse> items = response.body().getItems();
                    lessionList.clear();
                    lessionList.addAll(items);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PageResponse<LessionResponse>> call, Throwable t) {
                Log.e("API_FAILURE", "Error calling API", t);
            }
        });
    }
}

