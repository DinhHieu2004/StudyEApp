package com.example.myapplication.repositoris;


import android.content.Context;
import android.util.Log;

import com.example.myapplication.DAO.StatisticsDao;
import com.example.myapplication.DTO.response.StatisticsResponse;

import com.example.myapplication.Entitys.CategoryStatsEntity;
import com.example.myapplication.Entitys.ProgressStatsEntity;
import com.example.myapplication.Entitys.StatisticsEntity;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.example.myapplication.utils.AppDatabase;
import com.example.myapplication.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsRepository {
    private static final String TAG = "StatisticsRepository";

    private final StatisticsDao statisticsDao;
    private final ApiService apiService;
    private final ExecutorService executor;
    private final Context context;

    public StatisticsRepository(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase database = AppDatabase.getDatabase(this.context);
        this.statisticsDao = database.statisticsDao();
        this.apiService = ApiClient.getClient(this.context).create(ApiService.class);
        this.executor = Executors.newFixedThreadPool(2);
    }

    /**
     * Interface callback để trả về kết quả
     */
    public interface StatisticsCallback {
        void onSuccess(StatisticsData data, boolean isFromCache);
        void onError(String error);
    }

    /**
     * Class chứa dữ liệu thống kê đã được xử lý
     */
    public static class StatisticsData {
        public final StatisticsEntity statistics;
        public final List<CategoryStatsEntity> categoryStats;
        public final List<ProgressStatsEntity> progressStats;

        public StatisticsData(StatisticsEntity statistics,
                              List<CategoryStatsEntity> categoryStats,
                              List<ProgressStatsEntity> progressStats) {
            this.statistics = statistics;
            this.categoryStats = categoryStats;
            this.progressStats = progressStats;
        }
    }

    /**
     * Load dữ liệu thống kê (tự động chọn online/offline)
     */
    public void loadStatistics(StatisticsCallback callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            loadFromApi(callback);
        } else {
            loadFromDatabase(callback, true);
        }
    }

    /**
     * Load dữ liệu từ API
     */
    private void loadFromApi(StatisticsCallback callback) {
        Call<StatisticsResponse> call = apiService.statistics();

        call.enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatisticsResponse result = response.body();

                    // Lưu vào database trước
                    saveToDatabase(result, new SaveCallback() {
                        @Override
                        public void onSaved() {
                            // Sau khi lưu xong, load lại từ database để đảm bảo consistency
                            loadFromDatabase(callback, false);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error saving to database: " + error);
                            // Vẫn trả về dữ liệu từ API response
                            convertApiResponseToData(result, callback);
                        }
                    });
                } else {
                    // API failed, try loading from database
                    Log.w(TAG, "API response failed, loading from database");
                    loadFromDatabase(callback, true);
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                loadFromDatabase(callback, true);
            }
        });
    }

    /**
     * Load dữ liệu từ database
     */
    private void loadFromDatabase(StatisticsCallback callback, boolean isFromCache) {
        executor.execute(() -> {
            try {
                StatisticsEntity stats = statisticsDao.getLatestStatistics();

                if (stats != null) {
                    List<CategoryStatsEntity> categoryStats = statisticsDao.getAllCategoryStats();
                    List<ProgressStatsEntity> progressStats = statisticsDao.getAllProgressStats();

                    StatisticsData data = new StatisticsData(stats, categoryStats, progressStats);
                    callback.onSuccess(data, isFromCache);
                } else {
                    callback.onError(isFromCache ?
                            "Không có dữ liệu offline. Vui lòng kết nối mạng để tải dữ liệu." :
                            "Không thể tải dữ liệu từ server");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading from database", e);
                callback.onError("Lỗi đọc dữ liệu offline: " + e.getMessage());
            }
        });
    }

    /**
     * Interface callback cho việc lưu dữ liệu
     */
    private interface SaveCallback {
        void onSaved();
        void onError(String error);
    }

    /**
     * Lưu dữ liệu vào database
     */
    private void saveToDatabase(StatisticsResponse result, SaveCallback callback) {
        executor.execute(() -> {
            try {
                // Xóa dữ liệu cũ
                statisticsDao.deleteAllStatistics();
                statisticsDao.deleteAllCategoryStats();
                statisticsDao.deleteAllProgressStats();

                // Lưu thống kê tổng quan
                StatisticsEntity statsEntity = new StatisticsEntity(
                        result.getTotalQuestions(),
                        result.getCorrectAnswers(),
                        result.getAccuracyPercentage()
                );

                long statsId = statisticsDao.insertStatistics(statsEntity);

                // Lưu thống kê theo danh mục
                saveCategoryStats(result, statsId);

                // Lưu thống kê tiến độ
                saveProgressStats(result, statsId);

                Log.d(TAG, "Statistics saved to database successfully");
                callback.onSaved();

            } catch (Exception e) {
                Log.e(TAG, "Error saving statistics to database", e);
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Lưu thống kê theo danh mục
     */
    private void saveCategoryStats(StatisticsResponse result, long statsId) {
        try {
            Object categoryStatsObj = result.getCategoryStats();
            if (categoryStatsObj instanceof List) {
                List<?> categoryStats = (List<?>) categoryStatsObj;

                for (Object categoryObj : categoryStats) {
                    CategoryStatsEntity categoryEntity = new CategoryStatsEntity();
                    categoryEntity.statisticsId = statsId;
                    categoryEntity.categoryName = extractCategoryName(categoryObj);
                    categoryEntity.totalQuestions = extractTotalQuestions(categoryObj);
                    categoryEntity.correctAnswers = extractCorrectAnswers(categoryObj);
                    categoryEntity.accuracy = extractAccuracy(categoryObj);

                    statisticsDao.insertCategoryStats(categoryEntity);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving category stats", e);
        }
    }

    /**
     * Lưu thống kê tiến độ
     */
    private void saveProgressStats(StatisticsResponse result, long statsId) {
        try {
            Object progressStatsObj = result.getProgressStats();
            if (progressStatsObj instanceof List) {
                List<?> progressStats = (List<?>) progressStatsObj;

                for (Object progressObj : progressStats) {
                    ProgressStatsEntity progressEntity = new ProgressStatsEntity();
                    progressEntity.statisticsId = statsId;
                    progressEntity.date = extractProgressDate(progressObj);
                    progressEntity.accuracy = extractProgressAccuracy(progressObj);

                    statisticsDao.insertProgressStats(progressEntity);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving progress stats", e);
        }
    }

    /**
     * Convert API response thành StatisticsData (fallback method)
     */
    private void convertApiResponseToData(StatisticsResponse result, StatisticsCallback callback) {
        executor.execute(() -> {
            try {
                StatisticsEntity stats = new StatisticsEntity(
                        result.getTotalQuestions(),
                        result.getCorrectAnswers(),
                        result.getAccuracyPercentage()
                );

                // Convert category stats
                List<CategoryStatsEntity> categoryStats = new ArrayList<>();
                Object categoryStatsObj = result.getCategoryStats();
                if (categoryStatsObj instanceof List) {
                    List<?> categoryList = (List<?>) categoryStatsObj;
                    for (Object categoryObj : categoryList) {
                        CategoryStatsEntity entity = new CategoryStatsEntity();
                        entity.categoryName = extractCategoryName(categoryObj);
                        entity.totalQuestions = extractTotalQuestions(categoryObj);
                        entity.correctAnswers = extractCorrectAnswers(categoryObj);
                        entity.accuracy = extractAccuracy(categoryObj);
                        categoryStats.add(entity);
                    }
                }

                // Convert progress stats
                List<ProgressStatsEntity> progressStats = new ArrayList<>();
                Object progressStatsObj = result.getProgressStats();
                if (progressStatsObj instanceof List) {
                    List<?> progressList = (List<?>) progressStatsObj;
                    for (Object progressObj : progressList) {
                        ProgressStatsEntity entity = new ProgressStatsEntity();
                        entity.date = extractProgressDate(progressObj);
                        entity.accuracy = extractProgressAccuracy(progressObj);
                        progressStats.add(entity);
                    }
                }

                StatisticsData data = new StatisticsData(stats, categoryStats, progressStats);
                callback.onSuccess(data, false);

            } catch (Exception e) {
                callback.onError("Lỗi xử lý dữ liệu: " + e.getMessage());
            }
        });
    }

    // Helper methods để extract dữ liệu (copy từ StatisticsActivity)
    private String extractCategoryName(Object obj) {
        if (obj == null) return "Unknown Category";

        try {
            return (String) obj.getClass().getMethod("getCategoryName").invoke(obj);
        } catch (Exception e1) {
            try {
                return (String) obj.getClass().getField("categoryName").get(obj);
            } catch (Exception e2) {
                return "Unknown Category";
            }
        }
    }

    private double extractAccuracy(Object obj) {
        if (obj == null) return 0.0;

        try {
            Object result = obj.getClass().getMethod("getAccuracy").invoke(obj);
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            }
        } catch (Exception e1) {
            try {
                Object result = obj.getClass().getField("accuracy").get(obj);
                if (result instanceof Number) {
                    return ((Number) result).doubleValue();
                }
            } catch (Exception e2) {
                // Log warning
            }
        }
        return 0.0;
    }

    private int extractTotalQuestions(Object obj) {
        if (obj == null) return 0;

        try {
            Object result = obj.getClass().getMethod("getTotalQuestions").invoke(obj);
            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
        } catch (Exception e1) {
            try {
                Object result = obj.getClass().getField("totalQuestions").get(obj);
                if (result instanceof Number) {
                    return ((Number) result).intValue();
                }
            } catch (Exception e2) {
                // Log warning
            }
        }
        return 0;
    }

    private int extractCorrectAnswers(Object obj) {
        if (obj == null) return 0;

        try {
            Object result = obj.getClass().getMethod("getCorrectAnswers").invoke(obj);
            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
        } catch (Exception e1) {
            try {
                Object result = obj.getClass().getField("correctAnswers").get(obj);
                if (result instanceof Number) {
                    return ((Number) result).intValue();
                }
            } catch (Exception e2) {
                // Log warning
            }
        }
        return 0;
    }

    private String extractProgressDate(Object obj) {
        if (obj == null) return "Unknown";

        try {
            return (String) obj.getClass().getMethod("getDate").invoke(obj);
        } catch (Exception e1) {
            try {
                return (String) obj.getClass().getField("date").get(obj);
            } catch (Exception e2) {
                return "Unknown";
            }
        }
    }

    private double extractProgressAccuracy(Object obj) {
        if (obj == null) return 0.0;

        try {
            Object result = obj.getClass().getMethod("getAccuracy").invoke(obj);
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            }
        } catch (Exception e1) {
            try {
                Object result = obj.getClass().getField("accuracy").get(obj);
                if (result instanceof Number) {
                    return ((Number) result).doubleValue();
                }
            } catch (Exception e2) {
                // Log warning
            }
        }
        return 0.0;
    }

    /**
     * Dọn dẹp tài nguyên
     */
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}