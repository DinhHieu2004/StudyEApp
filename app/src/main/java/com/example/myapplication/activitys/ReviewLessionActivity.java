package com.example.myapplication.activitys;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DTO.response.LessionResponse;
import com.example.myapplication.R;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewLessionActivity extends AppCompatActivity {

    private RecyclerView rvLesson;
    private List<LessionResponse> lessonList;
    private ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson);

        rvLesson = findViewById(R.id.rvTopics);
        btnBack = findViewById(R.id.btnBack);
        rvLesson.setLayoutManager(new GridLayoutManager(this, 2));

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getListLessonWatch().enqueue(new Callback<List<LessionResponse>>() {
            @Override
            public void onResponse(Call<List<LessionResponse>> call, Response<List<LessionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lessonList = response.body();
                    rvLesson.setAdapter(new TopicAdapter(lessonList, topic -> {
                        Intent intent = new Intent(ReviewLessionActivity.this, FlashcardActivity.class);
                        intent.putExtra("lessonId", topic.getId());
                        intent.putExtra("lessionTitle", topic.getTitle());
                        Log.d("SEND_INTENT", "Sending id = " + topic.getId());
                        startActivity(intent);
                    }));
                }
            }

            @Override
            public void onFailure(Call<List<LessionResponse>> call, Throwable t) {
                Toast.makeText(ReviewLessionActivity.this, "Không thể tải danh sách chủ đề", Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewLessionActivity.this, MainActivity.class);
            intent.putExtra("openProfileFragment", true);
            startActivity(intent);
            finish();
        });
    }


    private static class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.LessonViewHolder> {

        private final List<LessionResponse> topicList;
        private final Consumer<LessionResponse> onClick;

        TopicAdapter(List<LessionResponse> topicList, Consumer<LessionResponse> onClick) {
            this.topicList = topicList;
            this.onClick = onClick;
        }

        @NonNull
        @Override
        public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
            return new LessonViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
            LessionResponse topic = topicList.get(position);
            holder.tvTopicName.setText(topic.getTitle());
            holder.itemView.setOnClickListener(v -> onClick.accept(topic));
        }

        @Override
        public int getItemCount() {
            return topicList.size();
        }

        static class LessonViewHolder extends RecyclerView.ViewHolder {
            TextView tvTopicName;

            public LessonViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTopicName = itemView.findViewById(R.id.tvTopicName);
            }
        }
    }
}
