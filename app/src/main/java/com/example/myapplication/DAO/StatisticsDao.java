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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertStatistics(StatisticsEntity stat);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategoryStats(List<CategoryStatsEntity> categories);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProgressStats(List<ProgressStatsEntity> progresses);

    @Query("SELECT * FROM statistics ORDER BY date DESC LIMIT 1")
    StatisticsEntity getLatestStatistics();

    @Query("SELECT * FROM category_stats WHERE statisticsId = :statId")
    List<CategoryStatsEntity> getCategoriesForStat(int statId);

    @Query("SELECT * FROM progress_stats WHERE statisticsId = :statId")
    List<ProgressStatsEntity> getProgressForStat(int statId);
}
