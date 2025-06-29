package com.example.myapplication.repositoris;


import android.content.Context;
import android.util.Log;


import com.example.myapplication.DAO.AnswerDetailDao;
import com.example.myapplication.DAO.QuizResultDao;
import com.example.myapplication.DTO.AnswerDetail;
import com.example.myapplication.DTO.QuizResult;
import com.example.myapplication.Entitys.QuizResultEntity;
import com.example.myapplication.Entitys.AnswerDetailEntity;
import com.example.myapplication.services.ApiService;
import com.example.myapplication.utils.ApiClient;
import com.example.myapplication.utils.AppDatabase;
import com.example.myapplication.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizHistoryRepository {
    private static final String TAG = "QuizHistoryRepository";

    private final QuizResultDao quizHistoryDao;

    private final AnswerDetailDao answerDetailDao;
    private final ApiService apiService;
    private final ExecutorService executor;
    private final Context context;
    private final AppDatabase database;


    public QuizHistoryRepository(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getDatabase(this.context);
        this.quizHistoryDao = database.quizHistoryDao();
        this.answerDetailDao = database.answerDetailDao();
        this.apiService = ApiClient.getClient(this.context).create(ApiService.class);
        this.executor = Executors.newFixedThreadPool(2);
    }


    /**
     * Interface callback để trả về kết quả
     */
    public interface QuizHistoryCallback {
        void onSuccess(List<QuizResult> results, boolean isFromCache);
        void onError(String error);
    }

    /**
     * Load dữ liệu quiz history (tự động chọn online/offline)
     */
    public void loadQuizHistory(String startDate, String endDate, QuizHistoryCallback callback) {
        // Luôn load từ cache trước để có response nhanh
        loadFromDatabase(startDate, endDate, new QuizHistoryCallback() {
            @Override
            public void onSuccess(List<QuizResult> cachedResults, boolean isFromCache) {
                // Trả về cached data ngay lập tức
                callback.onSuccess(cachedResults, true);

                // Sau đó sync với server nếu có mạng
                if (NetworkUtils.isNetworkAvailable(context)) {
                    syncWithServer(startDate, endDate, callback);
                }
            }

            @Override
            public void onError(String error) {
                // Nếu không có cache, thử load từ server
                if (NetworkUtils.isNetworkAvailable(context)) {
                    loadFromApi(startDate, endDate, callback);
                } else {
                    callback.onError("Không có dữ liệu offline và không có kết nối mạng");
                }
            }
        }, true);
    }



    /**
     * Load dữ liệu từ API
     */

    private void syncWithServer(String startDate, String endDate, QuizHistoryCallback callback) {
        Call<List<QuizResult>> call = apiService.getQuizHistory(startDate, endDate);

        call.enqueue(new Callback<List<QuizResult>>() {
            @Override
            public void onResponse(Call<List<QuizResult>> call, Response<List<QuizResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResult> freshResults = response.body();

                    Log.d(TAG, "Received " + freshResults.size() + " results from API");


                    // Update cache
                    saveToDatabase(freshResults, new SaveCallback() {
                        @Override
                        public void onSaved() {
                            // Trả về fresh data
                            callback.onSuccess(freshResults, false);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error updating cache: " + error);
                            // Vẫn trả về fresh data từ API
                            callback.onSuccess(freshResults, false);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<QuizResult>> call, Throwable t) {
                Log.w(TAG, "Sync failed, using cached data", t);
                // Không làm gì, user đã có cached data
            }
        });
    }
    private void saveToDatabase(List<QuizResult> results, SaveCallback callback) {
        executor.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    // Xóa dữ liệu cũ
                    quizHistoryDao.deleteAllQuizResults();
                    answerDetailDao.deleteAllAnswerDetails();

                    // Lưu dữ liệu mới
                    for (QuizResult result : results) {
                        QuizResultEntity entity = new QuizResultEntity(
                                result.getScore(),
                                result.getTotal(),
                                result.getDuration(),
                                result.getTimestamp()
                        );

                        long quizId = quizHistoryDao.insertQuizResult(entity);
                        saveAnswerDetails(result.getAnswers(), quizId);
                    }
                });

                Log.d(TAG, "Quiz history saved successfully");
                callback.onSaved();

            } catch (Exception e) {
                Log.e(TAG, "Error saving to database", e);
                callback.onError(e.getMessage());
            }
        });
    }
    private void loadFromApi(String startDate, String endDate, QuizHistoryCallback callback) {
        Call<List<QuizResult>> call = apiService.getQuizHistory(startDate, endDate);

        call.enqueue(new Callback<List<QuizResult>>() {
            @Override
            public void onResponse(Call<List<QuizResult>> call, Response<List<QuizResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResult> results = response.body();

                    // Lưu vào database trước
                    saveToDatabase(results, new SaveCallback() {
                        @Override
                        public void onSaved() {
                            // Sau khi lưu xong, load lại từ database để đảm bảo consistency
                            loadFromDatabase(startDate, endDate, callback, false);
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Error saving to database: " + error);
                            // Vẫn trả về dữ liệu từ API response
                            callback.onSuccess(results, false);
                        }
                    });
                } else {
                    // API failed, try loading from database
                    Log.w(TAG, "API response failed, loading from database");
                    loadFromDatabase(startDate, endDate, callback, true);
                }
            }

            @Override
            public void onFailure(Call<List<QuizResult>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                loadFromDatabase(startDate, endDate, callback, true);
            }
        });
    }

    /**
     * Load dữ liệu từ database
     */
    private void loadFromDatabase(String startDate, String endDate, QuizHistoryCallback callback, boolean isFromCache) {
        executor.execute(() -> {
            try {
                List<QuizResultEntity> entities;
                if (startDate != null && endDate != null) {
                    entities = quizHistoryDao.getQuizResults(startDate, endDate);
                } else {
                    entities = quizHistoryDao.getAllQuizResults();
                }

                if (!entities.isEmpty()) {
                    List<QuizResult> results = convertEntitiesToResults(entities);
                    callback.onSuccess(results, isFromCache);
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
    public void clearCache() {
        executor.execute(() -> {
            try {
                quizHistoryDao.deleteAllQuizResults();
                answerDetailDao.deleteAllAnswerDetails();
                Log.d(TAG, "Cache cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing cache", e);
            }
        });
    }

    /**
     * Thêm method để check cache size
     */
    public void getCacheInfo(CacheInfoCallback callback) {
        executor.execute(() -> {
            try {
                int quizCount = quizHistoryDao.getAllQuizResults().size();
                callback.onResult(quizCount);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public interface CacheInfoCallback {
        void onResult(int cacheSize);
        void onError(String error);
    }

    /**
     * Lưu answer details cho một quiz result
     */
    private void saveAnswerDetails(List<AnswerDetail> answers, long quizId) {
        try {
            if (answers != null && !answers.isEmpty()) {
                List<AnswerDetailEntity> answerEntities = new ArrayList<>();



                for (AnswerDetail answer : answers) {
                    // Convert List<String> to JSON string
                    String optionsJson = "";
                    if (answer.getOptions() != null) {
                        JSONArray jsonArray = new JSONArray();

                        Log.d(TAG, "Saving answer for quizId = " + quizId + ": " + answer.getQuestionText());

                        for (String option : answer.getOptions()) {
                            jsonArray.put(option);
                        }
                        optionsJson = jsonArray.toString();
                    }

                    AnswerDetailEntity answerEntity = new AnswerDetailEntity(
                            (int) quizId,
                            answer.getQuestionText(),
                            optionsJson,
                            answer.getCorrectAnswer(),
                            answer.getUserAnswer(),
                            answer.getCategory()
                    );
                    answerEntities.add(answerEntity);
                }

                answerDetailDao.insertAnswerDetails(answerEntities);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving answer details", e);
        }
    }

    /**
     * Convert entities thành QuizResult objects
     */
    private List<QuizResult> convertEntitiesToResults(List<QuizResultEntity> entities) {
        List<QuizResult> results = new ArrayList<>();

        for (QuizResultEntity entity : entities) {
            QuizResult result = new QuizResult();
            result.setScore(entity.getScore());
            result.setTotal(entity.getTotal());
            result.setDuration(entity.getDuration());
            result.setTimestamp(entity.getTimestamp());

            // Get answer details
            List<AnswerDetailEntity> answerEntities =
                    answerDetailDao.getAnswerDetailsByQuizId(entity.getId());

            List<AnswerDetail> answers = new ArrayList<>();
            for (AnswerDetailEntity answerEntity : answerEntities) {
                AnswerDetail answer = new AnswerDetail();
                answer.setQuestionText(answerEntity.getQuestionText());

                // Convert JSON string back to List<String>
                List<String> options = new ArrayList<>();
                try {
                    if (answerEntity.getOptions() != null && !answerEntity.getOptions().isEmpty()) {
                        JSONArray jsonArray = new JSONArray(answerEntity.getOptions());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            options.add(jsonArray.getString(i));
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing options JSON", e);
                }

                answer.setOptions(options);
                answer.setCorrectAnswer(answerEntity.getCorrectAnswer());
                answer.setUserAnswer(answerEntity.getUserAnswer());
                answer.setCategory(answerEntity.getCategory());
                answers.add(answer);
            }

            result.setAnswers(answers);
            results.add(result);
        }

        return results;
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