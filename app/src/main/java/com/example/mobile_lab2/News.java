package com.example.mobile_lab2;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "news")
public class News {
    @PrimaryKey(autoGenerate = true)
    private Integer mID;

    @ColumnInfo(name = "epochDate")
    private long mEpochDate;

    @ColumnInfo(name = "date")
    private String mNewsDate;

    @ColumnInfo(name = "header")
    private String mNewsHeader;

    @ColumnInfo(name = "summary")
    private String mNewsSummary;

    @ColumnInfo(name = "link")
    private String mNewsLink;

    @ColumnInfo(name = "image")
    private String mImageLink;

    @ColumnInfo(name = "markAsRead")
    private boolean mMarkAsRead;


    public News(String newsDate, String newsHeader, String newsSummary, String newsLink, String imageLink) {
        this.mNewsDate = newsDate;
        this.mNewsHeader = newsHeader;
        this.mNewsSummary = newsSummary;
        this.mNewsLink = newsLink;
        this.mImageLink = imageLink;
        this.mEpochDate = this.convertDateToEpoch(newsDate);
        this.mMarkAsRead = false;
    }

    public Integer getID() {
        return this.mID;
    }

    public void setID(Integer id) {
        this.mID = id;
    }

    public long getEpochDate() {
        return this.mEpochDate;
    }

    public void setEpochDate(long epochDate) {
        this.mEpochDate = epochDate;
    }

    public String getNewsDate() {
        return this.mNewsDate;
    }

    public void setNewsDate(String date) {
        this.mNewsDate = date;
    }

    public String getNewsHeader() {
        return this.mNewsHeader;
    }

    public void setNewsHeader(String header) {
        this.mNewsHeader = header;
    }

    public String getNewsSummary() {
        return this.mNewsSummary;
    }

    public void setNewsSummary(String summary) {
        this.mNewsSummary = summary;
    }

    public String getNewsLink() {
        return this.mNewsLink;
    }

    public void setNewsLink(String newsLink) {
        this.mNewsLink = newsLink;
    }

    public String getImageLink() {
        return this.mImageLink;
    }

    public void setImageLink(String imageLink) {
        this.mImageLink = imageLink;
    }

    public void setMarkAsRead(boolean bol) {
        this.mMarkAsRead = bol;
    }

    public boolean isMarkAsRead() {
        return this.mMarkAsRead;
    }

    public long convertDateToEpoch(String newsDate) {
        long tempDate = 0;

        try {
            DateFormat formatter =
                    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
            Date date = formatter.parse(newsDate);
            tempDate = date.getTime();
        } catch (ParseException e) {
            Log.d("Date", e.getMessage());
        }

        return tempDate;
    }
}
