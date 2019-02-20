package com.example.mobile_lab2;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {News.class}, version = 1 , exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    public abstract NewsDao daoAccess();
}