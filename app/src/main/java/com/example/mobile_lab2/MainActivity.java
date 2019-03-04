package com.example.mobile_lab2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String SERVICE_ACTION_RSS = "SERVICE_ACTION_RSS";
    private RecyclerView mRecyclerView;                                                         // The RecycleView.
    private RecyclerView.Adapter mAdapter;                                                      // Adapter to the RecycleView.
    private RecyclerView.LayoutManager mLayoutManager;                                          // Layout manager for RecycleView.
    private SearchView mSearchView;                                                             // Search bar.
    private DatabaseWrapper mDB;                                                                // Database.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);                                                 // Sets the content to be from 'activity_main'.
        Toolbar toolbar =  findViewById(R.id.toolbar);                                          // Gets the toolbar.
        setSupportActionBar(toolbar);                                                           // Enables the toolbar.
        setTitle("News reader");                                                                // Sets the title of the toolbar.

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);                                // Gets the drawer.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(                               // For opening and closing the drawer.
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);                                                       // Listener for the drawer.
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);                            // Gets the navigation bar.
        navigationView.setNavigationItemSelectedListener(this);                                 // Listener on nav bar.

        mRecyclerView = findViewById(R.id.contentView);                                         // Gets the RecyclerView.
        mRecyclerView.setHasFixedSize(true);                                                    // Optimizing.

        mLayoutManager = new LinearLayoutManager(this);                                  // Connects the RecycleView.
        mRecyclerView.setLayoutManager(mLayoutManager);

        populateNewsAdapter();                                                                  // Populates the recycle view.
        startPendingBackgroundService();                                                        // Starts the service to fetch RSS feeds.

        /* ToDo: Må gjøres:
        [X] - Kjør servicen i bakgrunn
            [X] - Bruk constraints fra settings.
        [X] - Lag en preferences side med:
            [X] - Hvor mange items som skal vises i news listen (10, 20, 50, 70, 100)
            [X] - Hvor ofte nye feeds skal legges inn (10m, 60m, once a day...)
            [X] - RSS feed
        [ ] - Legg til søk som skal søke etter artikkler som matcher ett sett "pattern" (regex)?
        [ ] - Unit tests
        */

        /* ToDo: Nice to have:
        [ ] - Chache nyheter og bilder.
        [ ] - Slett en news fra news lsiten (skal ikke komme opp igjen ved neste fetch)
        [ ] - Bruk API "https://cloud.feedly.com/v3/search/feeds/?query=nrk.no" for å søke etter feeds. Kan kjøre som Async.
        [ ] - Valider RSS feeden som kommer opp, må støtet rss v.2 (eller har støtte for å parse forskjellige rss feeds)
        [ ] - Display rss feeds på en penere måte (e.g: vg.no - toppsaker)
        [ ] - Velg hvilke rss feeds som skal vises med en toggle i listen.
        [ ] - Hvis antall news i title.
        */
    }

    public static void startBackgroundService(Context context) {                                // Starts the background service directly.
        Intent intent = new Intent(context, RSSPullService.class);
        context.startService(intent);
    }

    public void startPendingBackgroundService() {                                               // Starts and kills service every x minutes.
        SharedPreferences sharedPreferences =                                                   // Connects to shared preferences.
                getSharedPreferences(
                        userPreferencesActivity.SHARED_PREFS_SETTINGS,
                        Context.MODE_PRIVATE
                );

        String syncInterval =                                                                   // Gets sync interval from shared prefs.
                sharedPreferences.getString("syncIntervalNews", "24 hours");        // Defaults to sync every 24 hours.

        int mili = 86400000;        // Sets default to 24 hours.

        switch (syncInterval) {
            case "10 min":
                mili = 600000;      // 10 minutes in milliseconds.
                break;
            case "30 min":
                mili = 1800000;     // 30 minutes in milliseconds.
                break;
            case "1 hour":
                mili = 3600000;     // 1 hour in milliseconds.
                break;
            case "5 hours":
                mili = 18000000;    // 5 hours in milliseconds.
                break;
            case "12 hours":
                mili = 43200000;    // 12 hours in milliseconds.
                break;
            case "24 hours":
                mili = 86400000;    // 24 hours in milliseconds.
                break;
        }

        Intent intent = new Intent(this, RSSPullService.class);
        PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP,                                             // Service starts every x minutes.
                System.currentTimeMillis(), mili, pIntent);
    }

    public void populateNewsAdapter() {                                                         // Add news to recycle view.
        mDB = new DatabaseWrapper(this, "news");                                  // Connects the the database.
        SharedPreferences sharedPreferences =                                                   // Connects to shared preferences.
                getSharedPreferences(
                        userPreferencesActivity.SHARED_PREFS_SETTINGS,
                        Context.MODE_PRIVATE
                );

        int numberOfNews = sharedPreferences.getInt("numberOfNewsToShow", 10);      // Number of news to show.
        News[] tempNews = mDB.getNewsFromDB(numberOfNews);                                      // Get news from db.
        ArrayList<News> news = new ArrayList<>(Arrays.asList(tempNews));                        // Convert List to ArrayList.
        mAdapter = new ContentAdapter(news);                                                    // Adds content to RecycleView.
        mRecyclerView.setAdapter(mAdapter);                                                     // Attaches the RecycleView.
    }

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver,
                new IntentFilter(SERVICE_ACTION_RSS));
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {                             // Broadcast receiver to receive from Service.
        @Override
        public void onReceive(Context context, Intent intent) {
            populateNewsAdapter();
        }
    };

    @Override
    public void onBackPressed() {                                                               // Handles back press on nav bar:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                             // Handles toolbar options (Search):
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);                         // Gets the search from the menu.
        mSearchView = (SearchView) myActionMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {               // Listener for text changes.
            @Override
            public boolean onQueryTextSubmit(String query) {                                    // When query is submitted:

                // ToDo: Do something with query.
                
                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();

                if( ! mSearchView.isIconified()) {                                              // Removes focus from search.
                    mSearchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();                                          // Clears the search text.
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {                                    // Handles navigation bar item clicks:
        int id = item.getItemId();

        if (id == R.id.home) {                                                                  // Returns to main screen when 'Home' is clicked.
            return true;
        } else if (id == R.id.settings) {                                                       // Starts the userPrefs activity.
            Intent intent = new Intent(this, userPreferencesActivity.class);
            startActivity(intent);
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
