package com.example.myapplication.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.DAO.CategoryStatsDao;
import com.example.myapplication.DAO.ProgressStatsDao;
import com.example.myapplication.DAO.StatisticsDao;
import com.example.myapplication.Entitys.CategoryStatsEntity;
import com.example.myapplication.Entitys.ProgressStatsEntity;
import com.example.myapplication.Entitys.StatisticsEntity;

@Database(
        entities = {StatisticsEntity.class, CategoryStatsEntity.class, ProgressStatsEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract StatisticsDao statisticsDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "statistics_database"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Method để xóa database (useful for testing)
    public static void destroyInstance() {
        INSTANCE = null;
    }
}