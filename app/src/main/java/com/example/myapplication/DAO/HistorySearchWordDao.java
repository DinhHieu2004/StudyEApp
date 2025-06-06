package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.myapplication.Entitys.HistorySearchWord;

import java.util.List;

@Dao
public interface HistorySearchWordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HistorySearchWord historySearchWord);

    @Query("SELECT * FROM history_search_word ORDER BY searched_at DESC")
    List<HistorySearchWord> getAllHistory();

    @Query("SELECT * FROM history_search_word WHERE word = :word LIMIT 1")
    HistorySearchWord getWord(String word);

    @Delete
    void delete(HistorySearchWord historySearchWord);

    @Query("DELETE FROM history_search_word")
    void deleteAll();

    @Query("SELECT * FROM history_search_word WHERE word = :word LIMIT 1")
    HistorySearchWord findByWord(String word);

    @Query("SELECT word FROM history_search_word ORDER BY id DESC")
    List<String> getAllWords();
}

