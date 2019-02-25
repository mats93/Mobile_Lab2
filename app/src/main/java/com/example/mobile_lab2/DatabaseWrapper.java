package com.example.mobile_lab2;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

// Wrapper class for removing AsyncTask clutter in main activity
// We need to use AsyncTask here to talk to the DB because it will throw
// exceptions when accessed on the main thread. This is because DB lookups
// can take a while or even throw other exceptions and we don't want the
// UI to freeze.

@SuppressLint("StaticFieldLeak")
public class DatabaseWrapper {
    private RoomDB mDB;
    private String mName;

    public DatabaseWrapper(Context context, String dbName) {
        mName = dbName;
        mDB = Room.databaseBuilder(context, RoomDB.class, mName).build();
    }

    public void DropTable() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mDB.daoAccess().dropTable();
                return null;
            }
        }.execute();
    }

    public void InsertToDB(News... news) {
        new AsyncTask<News, Void, Void>() {
            @Override
            protected Void doInBackground(News... news) {
                mDB.daoAccess().insert(news);
                return null;
            }
        }.execute(news);
    }

    public News[] getAllNewsFromDB() {
        News[] temp = null;
        try {
            temp = new AsyncTask<Void, Void, News[]>() {
                @Override
                protected News[] doInBackground(Void... voids) {
                    return mDB.daoAccess().getAllNews();
                }
            }.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public News[] getNewsNewerThenDate(Integer date) {
        News[] temp = null;
        try {
            temp = new AsyncTask<Integer, Void, News[]>() {
                @Override
                protected News[] doInBackground(Integer... integers) {
                    return mDB.daoAccess().newsNewerThenDate(integers[0]);
                }
            }.execute(date).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void markAsRead(String link) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings){
                mDB.daoAccess().markAsRead(strings[0]);
                return null;
            }
        }.execute(link);
    }

    public boolean isMarkedAsRead(String link) {
        Integer temp = 0;
        try {
            temp = new AsyncTask<String, Void, Integer>() {
                @Override
                protected Integer doInBackground(String... strings) {
                    return mDB.daoAccess().isRead(strings[0]);
                }
            }.execute(link).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (temp == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean articleExistsAlready(String link) {
        Integer temp = 0;
        try {
            temp = new AsyncTask<String, Void, Integer>() {
                @Override
                protected Integer doInBackground(String... strings) {
                    return mDB.daoAccess().articleExistsAlready(strings[0]);
                }
            }.execute(link).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (temp == 0) {
            return false;
        } else {
            return true;
        }
    }
}
