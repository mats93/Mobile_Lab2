package com.example.mobile_lab2;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
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

    public News[] getNewsFromDB(Integer num) {
        News[] temp = null;
        try {
            temp = new AsyncTask<Integer, Void, News[]>() {
                @Override
                protected News[] doInBackground(Integer... integers) {
                    return mDB.daoAccess().getNews(integers[0]);
                }
            }.execute(num).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public News[] getFilteredNewsFromDB(String query) {
        String useQuery = "%" + query + "%";

        News[] temp = null;
        try {
            temp = new AsyncTask<String, Void, News[]>() {
                @Override
                protected News[] doInBackground(String... strings) {
                    return mDB.daoAccess().getFilteredNews(strings[0]);
                }
            }.execute(useQuery).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void markAsRead(String link) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
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

    public void deleteNewsOlderThen(String days) {

        // "'%s', 'now', '-:days day'";
        String dateStatement = "'%s', 'now' '-" + days + " day'";

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                mDB.daoAccess().deleteNewsOlderThen(dateStatement);
                return null;
            }
        }.execute(days);
    }
}
