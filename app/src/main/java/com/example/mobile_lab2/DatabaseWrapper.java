package com.example.mobile_lab2;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

// Wrapper class for removing AsyncTask clutter in main activity
// We need to use AsyncTask here to talk to the DB because it will throw
// exceptions when accessed on the main thread. This is because DB lookups
// can take a while or even throw other exceptions and we don't want the
// UI to freeze.

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
        }.execute();
    }
}
