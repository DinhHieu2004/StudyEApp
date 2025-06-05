package com.example.myapplication.activitys;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.CategoryStatsAdapter;
import com.example.myapplication.repositoris.StatisticsRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "StatisticsActivity";

    // UI Components
    private TextView txtTotalQuestions, txtCorrectAnswers, txtAccuracy, txtDataSource;
    private PieChart pieChart;
    private BarChart barChart;
    private LineChart lineChart;
    private RecyclerView recyclerViewCategories;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    // Adapter and Data
    private CategoryStatsAdapter categoryAdapter;
    private List<CategoryStats> categoryStatsList;

    // Repository
    private StatisticsRepository statisticsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        initRepository();
        initViews();
        setupRecyclerView();
        loadStatistics();
    }

    private void initRepository() {
        statisticsRepository = new StatisticsRepository(this);
    }

    private void initViews() {
        txtTotalQuestions = findViewById(R.id.txtTotalQuestions);
        txtCorrectAnswers = findViewById(R.id.txtCorrectAnswers);
        txtAccuracy = findViewById(R.id.txtAccuracy);
        txtDataSource = findViewById(R.id.txtDataSource);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        lineChart = findViewById(R.id.lineChart);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        categoryStatsList = new ArrayList<>();
        categoryAdapter = new CategoryStatsAdapter(categoryStatsList);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void loadStatistics() {
        showLoading(true);

        statisticsRepository.loadStatistics(new StatisticsRepository.StatisticsCallback() {
            @Override
            public void onSuccess(StatisticsRepository.StatisticsData data, boolean isFromCache) {
                runOnUiThread(() -> {
                    showLoading(false);
                    updateDataSourceIndicator(isFromCache);
                    displayStatistics(data);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError(error);
                    clearAllCharts();
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void updateDataSourceIndicator(boolean isFromCache) {
        if (isFromCache) {
            txtDataSource.setText("Dữ liệu offline");
            txtDataSource.setTextColor(Color.parseColor("#FF9800"));
        } else {
            txtDataSource.setText("Dữ liệu từ server");
            txtDataSource.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    private void displayStatistics(StatisticsRepository.StatisticsData data) {
        try {
            // Display overview statistics
            displayOverviewStatistics(data.statistics);

            // Setup charts
            setupPieChart(data.statistics);
            setupBarChart(data.categoryStats);
            setupLineChart(data.progressStats);

            // Update category list
            updateCategoryList(data.categoryStats);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying statistics", e);
            showError("Lỗi hiển thị dữ liệu: " + e.getMessage());
        }
    }

    private void displayOverviewStatistics(com.example.myapplication.Entitys.StatisticsEntity stats) {
        txtTotalQuestions.setText(String.valueOf(stats.totalQuestions));
        txtCorrectAnswers.setText(String.valueOf(stats.correctAnswers));
        txtAccuracy.setText(new DecimalFormat("#.#").format(stats.accuracyPercentage) + "%");
    }

    private void setupPieChart(com.example.myapplication.Entitys.StatisticsEntity stats) {
        int correct = (int) stats.correctAnswers;
        int incorrect = (int) (stats.totalQuestions - stats.correctAnswers);

        if (correct + incorrect == 0) {
            pieChart.setNoDataText("Chưa có dữ liệu");
            pieChart.clear();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(correct, "Đúng"));
        entries.add(new PieEntry(incorrect, "Sai"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{Color.parseColor("#4CAF50"), Color.parseColor("#F44336")});
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setCenterText("Kết quả\nTổng quan");
        pieChart.setCenterTextSize(14f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBarChart(List<com.example.myapplication.Entitys.CategoryStatsEntity> categoryStats) {
        if (categoryStats == null || categoryStats.isEmpty()) {
            barChart.setNoDataText("Chưa có dữ liệu phân loại");
            barChart.clear();
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < categoryStats.size(); i++) {
            com.example.myapplication.Entitys.CategoryStatsEntity category = categoryStats.get(i);
            entries.add(new BarEntry(i, (float) category.accuracy));

            String categoryName = category.categoryName;
            labels.add(categoryName.length() > 15 ?
                    categoryName.substring(0, 15) + "..." : categoryName);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Độ chính xác theo danh mục (%)");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.8f);

        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setTextSize(10f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextSize(10f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.invalidate();
    }

    private void setupLineChart(List<com.example.myapplication.Entitys.ProgressStatsEntity> progressStats) {
        if (progressStats == null || progressStats.isEmpty()) {
            lineChart.setNoDataText("Chưa có dữ liệu tiến độ");
            lineChart.clear();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        for (int i = 0; i < progressStats.size(); i++) {
            com.example.myapplication.Entitys.ProgressStatsEntity progress = progressStats.get(i);
            entries.add(new Entry(i, (float) progress.accuracy));
            dates.add(formatDate(progress.date));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Tiến độ độ chính xác (%)");
        dataSet.setColor(Color.parseColor("#3F51B5"));
        dataSet.setCircleColor(Color.parseColor("#3F51B5"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(6f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#3F51B5"));
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(10f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setTextSize(10f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.invalidate();
    }

    private void updateCategoryList(List<com.example.myapplication.Entitys.CategoryStatsEntity> categoryStats) {
        categoryStatsList.clear();

        if (categoryStats != null) {
            for (com.example.myapplication.Entitys.CategoryStatsEntity category : categoryStats) {
                categoryStatsList.add(new CategoryStats(
                        category.categoryName,
                        category.totalQuestions,
                        category.correctAnswers,
                        category.accuracy
                ));
            }
        }

        categoryAdapter.notifyDataSetChanged();
    }

    private String formatDate(String date) {
        if (date == null || date.isEmpty()) return "N/A";

        try {
            return date.length() > 10 ? date.substring(5, 10) : date;
        } catch (Exception e) {
            return date;
        }
    }

    private void clearAllCharts() {
        // Clear text views
        txtTotalQuestions.setText("0");
        txtCorrectAnswers.setText("0");
        txtAccuracy.setText("0%");
        txtDataSource.setText("Không có dữ liệu");
        txtDataSource.setTextColor(Color.parseColor("#757575"));

        // Clear charts
        pieChart.setNoDataText("Không có dữ liệu");
        pieChart.clear();

        barChart.setNoDataText("Không có dữ liệu");
        barChart.clear();

        lineChart.setNoDataText("Không có dữ liệu");
        lineChart.clear();

        // Clear category list
        categoryStatsList.clear();
        categoryAdapter.notifyDataSetChanged();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (statisticsRepository != null) {
            statisticsRepository.cleanup();
        }
    }

    // Inner class for category statistics (unchanged)
    public static class CategoryStats {
        private String categoryName;
        private int totalQuestions;
        private int correctAnswers;
        private double accuracy;

        public CategoryStats(String categoryName, int totalQuestions, int correctAnswers, double accuracy) {
            this.categoryName = categoryName;
            this.totalQuestions = totalQuestions;
            this.correctAnswers = correctAnswers;
            this.accuracy = accuracy;
        }

        // Getters
        public String getCategoryName() { return categoryName; }
        public int getTotalQuestions() { return totalQuestions; }
        public int getCorrectAnswers() { return correctAnswers; }
        public double getAccuracy() { return accuracy; }
    }
}