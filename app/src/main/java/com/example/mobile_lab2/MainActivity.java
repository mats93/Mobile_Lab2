package com.example.mobile_lab2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.Collections;

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

        mDB = new DatabaseWrapper(this, "news");                                  // Connects the the database.

        //mDB.DropTable();
        News[] temp = mDB.getAllNewsFromDB();                                                   // Gets all news from the db.
        ArrayList<News> news = new ArrayList<>(Arrays.asList(temp));
        Collections.reverse(news);                                                              // Reverses the ArrayList. New news first.
        mAdapter = new ContentAdapter(news);                                                    // Adds content to RecycleView.
        mRecyclerView.setAdapter(mAdapter);                                                     // Attaches the RecycleView.


        // ToDo: Should not start the service here, but every x minutes.
        Intent intent = new Intent(MainActivity.this, RSSPullService.class);
        startService(intent);


        /* ToDo: Må gjøres:
        [ ] - Kjør fetch for hver av disse feedene.
        [ ] - Kjør servicen i bakgrunn
        [X] - Lag en preferences side med:
            [X] - Hvor mange items som skal vises i news listen (10, 20, 50, 70, 100)
            [X] - Hvor ofte nye feeds skal legges inn (10m, 60m, once a day...)
            [X] - RSS feed
        [ ] - Legg til constrains fra shared prefs i RSS service.
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

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver,
                new IntentFilter(SERVICE_ACTION_RSS));
    }

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {                             // Broadcast receiver to receive from Service.
        @Override
        public void onReceive(Context context, Intent intent) {
            News[] temp = mDB.getAllNewsFromDB();                                               // Gets all news from the db.
            ArrayList<News> news = new ArrayList<>(Arrays.asList(temp));
            Collections.reverse(news);                                                          // Reverses the ArrayList. New news first.
            mAdapter = new ContentAdapter(news);                                                // Adds content to RecycleView.
            mRecyclerView.setAdapter(mAdapter);                                                 // Attaches the RecycleView.
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
                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();              // TEMP: toast.

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
