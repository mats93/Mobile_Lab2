package com.example.mobile_lab2;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface NewsDao {
    @Insert
    public void insert(News... news);                               // Insert a new 'News' object.

    @Query("SELECT * FROM news ORDER BY epochDate DESC LIMIT :num") // Get news from the database.
    public News[] getNews(Integer num);

    @Query("UPDATE news SET markAsRead = 1 WHERE link = :link")     // Set markedAsRead to true.
    public void markAsRead(String link);

    @Query("SELECT markAsRead FROM news WHERE link = :link")        // Returns if news article is marked as read or not.
    public int isRead(String link);

    @Query("SELECT EXISTS(SELECT 1 FROM news WHERE link = :link)")  // Returns if news article exists already or not
    public int articleExistsAlready(String link);

    @Query("DELETE FROM news WHERE epochDate >= strftime(:days)")   // Delte old news from the database.
    public void deleteNewsOlderThen(String days);

    @Query("DELETE FROM news")                                      // Deletes everything from the database.
    public void dropTable();
}
