package com.example.myapplication.activitys;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.LessionAdapter;
import com.example.myapplication.model.Lessions;

import java.util.ArrayList;

public class LessionListActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterCourceList;
    private RecyclerView recyclerViewCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_list);

        initRecyclerView();
    }

    private void initRecyclerView() {
        ArrayList<Lessions> items = new ArrayList<>();
        items.add(new Lessions("Advanced certification Program in AI", 169, "ic_1"));
        items.add(new Lessions("Google Cloud Platform Architecture", 69, "ic_2"));
        items.add(new Lessions("Fundamental of Java Programming", 150, "ic_3"));
        items.add(new Lessions("Introduction to UI design history", 79, "ic_4"));
        items.add(new Lessions("PG Program in Big Data Engineering", 49, "ic_5"));

        recyclerViewCourse=findViewById(R.id.view);
        recyclerViewCourse.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapterCourceList = new LessionAdapter(items);
        recyclerViewCourse.setAdapter(adapterCourceList);
    }
}
