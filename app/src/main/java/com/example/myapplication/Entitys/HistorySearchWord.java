package com.example.myapplication.Entitys;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "history_search_word")
public class HistorySearchWord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "word")
    private String word;

    @ColumnInfo(name = "searched_at")
    private long searchedAt;

    @ColumnInfo(name = "json_data")
    private String jsonData;

    public HistorySearchWord(String word, long searchedAt, String jsonData) {
        this.word = word;
        this.searchedAt = searchedAt;
        this.jsonData = jsonData;
    }

    // getter setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(long searchedAt) {
        this.searchedAt = searchedAt;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
}

