package com.example.myapplication.DAO;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myapplication.Entitys.ProgressStatsEntity;

import java.util.List;

@Dao
public interface ProgressStatsDao {

    @Insert
    void insertAll(List<ProgressStatsEntity> progressStatsList);

    @Query("SELECT * FROM progress_stats WHERE statisticsId = :statId")
    List<ProgressStatsEntity> getByStatisticsId(int statId);
}
