package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Entitys.CategoryStatsEntity;

import java.util.List;

@Dao
public interface CategoryStatsDao {

    @Insert
    void insertAll(List<CategoryStatsEntity> categoryStatsList);

    @Query("SELECT * FROM category_stats WHERE statisticsId = :statId")
    List<CategoryStatsEntity> getByStatisticsId(int statId);
}
