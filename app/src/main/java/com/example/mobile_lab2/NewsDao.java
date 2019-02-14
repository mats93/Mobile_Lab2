package com.example.mobile_lab2;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

@Dao
public interface NewsDao {
    @Insert
    public void insert(News... news);                       // Insert a new 'News' object.

    @Update
    public void update(News... news);                       // Not used?

    @Query("SELECT * FROM news WHERE mID = :id")            // Not used?
    public News getNews(Integer id);

    @Query("SELECT image FROM news WHERE mID = :id")        // Get image reference in Internal Storage for 'id'.
    public String getImage(Integer id);

    @Query("SELECT * FROM news WHERE epochDate < :date")    // Get all 'News' newer then <epoch-date>.
    public News[] newsNewerThenDate(long date);

    @Query("DELETE FROM news WHERE epochDate <= :date")     // Delete all news older then a specific <epoch-date>.
    public void deleteNewsOlderThen(long date);

    @Query("DELETE FROM news")                              // Deletes everything from the database.
    public void dropTable();
}
