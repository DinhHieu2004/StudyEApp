//package com.example.myapplication.activitys;
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.example.myapplication.R;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.function.Consumer;
//
//public class SelectTopicActivity extends AppCompatActivity {
//
//    private RecyclerView rvTopics;
//    private List<String> topics;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select_topic);
//
//        rvTopics = findViewById(R.id.rvTopics);
//
//        topics = Arrays.asList("Animals", "Food", "Travel");
//
//        rvTopics.setLayoutManager(new LinearLayoutManager(this));
//        rvTopics.setAdapter(new TopicAdapter(topics, topic -> {
//            Intent intent = new Intent(SelectTopicActivity.this, FlashcardActivity.class);
//            intent.putExtra("topic", topic);
//            startActivity(intent);
//        }));
//    }
//
//    private static class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
//
//        private final List<String> topicList;
//        private final Consumer<String> onClick;
//
//        TopicAdapter(List<String> topicList, Consumer<String> onClick) {
//            this.topicList = topicList;
//            this.onClick = onClick;
//        }
//
//        @NonNull
//        @Override
//        public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
//            return new TopicViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
//            String topic = topicList.get(position);
//            holder.tvTopicName.setText(topic);
//            holder.itemView.setOnClickListener(v -> onClick.accept(topic));
//        }
//
//        @Override
//        public int getItemCount() {
//            return topicList.size();
//        }
//
//        static class TopicViewHolder extends RecyclerView.ViewHolder {
//            TextView tvTopicName;
//
//            public TopicViewHolder(@NonNull View itemView) {
//                super(itemView);
//                tvTopicName = itemView.findViewById(R.id.tvTopicName);
//            }
//        }
//    }
//}
