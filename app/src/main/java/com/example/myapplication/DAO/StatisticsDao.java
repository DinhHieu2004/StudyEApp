package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.Entitys.CategoryStatsEntity;
import com.example.myapplication.Entitys.ProgressStatsEntity;
import com.example.myapplication.Entitys.StatisticsEntity;

import java.util.List;


@Dao
public interface StatisticsDao {

    // Statistics operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStatistics(StatisticsEntity statistics);

    @Query("SELECT * FROM statistics ORDER BY lastUpdated DESC LIMIT 1")
    StatisticsEntity getLatestStatistics();

    @Query("DELETE FROM statistics")
    void deleteAllStatistics();

    @Query("SELECT COUNT(*) FROM statistics")
    int getStatisticsCount();

    // Category Stats operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCategoryStats(CategoryStatsEntity categoryStats);

    @Query("SELECT * FROM category_stats WHERE statisticsId = :statisticsId ORDER BY categoryName")
    List<CategoryStatsEntity> getCategoryStatsByStatisticsId(long statisticsId);

    @Query("SELECT cs.* FROM category_stats cs " +
            "INNER JOIN statistics s ON cs.statisticsId = s.id " +
            "WHERE s.id = (SELECT id FROM statistics ORDER BY lastUpdated DESC LIMIT 1) " +
            "ORDER BY cs.categoryName")
    List<CategoryStatsEntity> getAllCategoryStats();

    @Query("DELETE FROM category_stats")
    void deleteAllCategoryStats();

    // Progress Stats operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProgressStats(ProgressStatsEntity progressStats);

    @Query("SELECT * FROM progress_stats WHERE statisticsId = :statisticsId ORDER BY date")
    List<ProgressStatsEntity> getProgressStatsByStatisticsId(long statisticsId);

    @Query("SELECT ps.* FROM progress_stats ps " +
            "INNER JOIN statistics s ON ps.statisticsId = s.id " +
            "WHERE s.id = (SELECT id FROM statistics ORDER BY lastUpdated DESC LIMIT 1) " +
            "ORDER BY ps.date")
    List<ProgressStatsEntity> getAllProgressStats();

    @Query("DELETE FROM progress_stats")
    void deleteAllProgressStats();

    // Combined operations
    @Query("SELECT * FROM statistics WHERE lastUpdated > :timestamp ORDER BY lastUpdated DESC")
    List<StatisticsEntity> getStatisticsAfter(long timestamp);

    @Query("DELETE FROM statistics WHERE lastUpdated < :timestamp")
    void deleteOldStatistics(long timestamp);

    // Check if data exists
    @Query("SELECT EXISTS(SELECT 1 FROM statistics LIMIT 1)")
    boolean hasStatisticsData();

    @Query("SELECT EXISTS(SELECT 1 FROM category_stats LIMIT 1)")
    boolean hasCategoryStatsData();

    @Query("SELECT EXISTS(SELECT 1 FROM progress_stats LIMIT 1)")
    boolean hasProgressStatsData();

    // Get statistics with related data
    @Query("SELECT s.id, s.lastUpdated, s.correctAnswers, s.totalQuestions, " +
            "(SELECT COUNT(*) FROM category_stats cs WHERE cs.statisticsId = s.id) as categoryCount, " +
            "(SELECT COUNT(*) FROM progress_stats ps WHERE ps.statisticsId = s.id) as progressCount " +
            "FROM statistics s ORDER BY s.lastUpdated DESC LIMIT 1")
    StatisticsWithCounts getLatestStatisticsWithCounts();


    // Helper class for complex queries
    class StatisticsWithCounts {
        public long id;
        public long lastUpdated;
        public int correctAnswers;
        public int totalQuestions;

        public int categoryCount;
        public int progressCount;
    }
}
