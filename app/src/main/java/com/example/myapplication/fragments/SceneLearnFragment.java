package com.example.myapplication.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.DTO.response.LessionResponse;
import com.example.myapplication.DTO.response.PageResponse;
import com.example.myapplication.R;
import com.example.myapplication.adapters.SceneLearnAdapter;
import com.example.myapplication.services.LessionApiService;
import com.example.myapplication.utils.ApiClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SceneLearnFragment extends Fragment {

    private RecyclerView recyclerView;
    private SceneLearnAdapter adapter;
    private List<LessionResponse> lessionList = new ArrayList<>();

    private static final String TAG = "SceneLearnFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scene_learn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.sceneLearnView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 cột
        adapter = new SceneLearnAdapter(requireActivity(), lessionList);
        recyclerView.setAdapter(adapter);

        loadLessionsFromApi();
    }

    private void loadLessionsFromApi() {
        LessionApiService apiService = ApiClient.getClient(requireContext()).create(LessionApiService.class);

        Call<PageResponse<LessionResponse>> call = apiService.getLessions(1L, 0, 10); // topicId = 1
        call.enqueue(new Callback<PageResponse<LessionResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<LessionResponse>> call, Response<PageResponse<LessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LessionResponse> items = response.body().getItems();
                    Log.d(TAG, "Số bài học nhận được: " + items.size());

                    lessionList.clear();
                    lessionList.addAll(items);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Lỗi response hoặc rỗng");
                }
            }

            @Override
            public void onFailure(Call<PageResponse<LessionResponse>> call, Throwable t) {
                Log.e(TAG, "API gọi thất bại", t);
            }
        });
    }
}
