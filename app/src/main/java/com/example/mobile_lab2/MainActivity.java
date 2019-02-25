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
import android.util.Log;
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

        // ToDo: Implement database.
        mDB = new DatabaseWrapper(this, "news");

        //mDB.InsertToDB(new News("date", "Hello from DB",
        //       "This is a summary", "vg.no", "https://gfx.nrk.no/FPWgzpkxcBY29jwH7TIlgAJpWCcsl8C8Rd62b-jRZOsA"));

        //newsData.add(temp);

        //mAdapter = new ContentAdapter(newsData);
        //mRecyclerView.setAdapter(mAdapter);

        // DB testing....
        //mDB.DropTable();
        News[] temp = mDB.getAllNewsFromDB();
        ArrayList<News> tempArrayList = new ArrayList<>(Arrays.asList(temp));
        mAdapter = new ContentAdapter(tempArrayList);
        mRecyclerView.setAdapter(mAdapter);

        Toast.makeText(this, "News in DB: " + tempArrayList.size(), Toast.LENGTH_SHORT).show();


        // Starts up the service.
        // ToDo: Service should run every x minutes, also in the background when app is closed.
        Intent intent = new Intent(MainActivity.this, RSSPullService.class);
        startService(intent);


        /* ToDo - RSS feed lagring og visning:
        [X] - Hent inn alle saker fra databasen.
        [ ] - Kjør fetch for hver feed.
        [ ] - Hvis fetch nyhet sin dato >= siste element i databasen -> legg den til (Gjør dette for hver nye sak)
        [ ] - Slett alle database elementer hvor dato < siste fetchede nyhets dato.
        [ ] - Display nye saker
        [X] - Endre farge på de nyhetene som er klikket på.
        [ ] - Long click -> slett nyheten fra DB og view.
        */

        /* ToDo - RSS feed adding og sletting:
        [ ] - Bruk API "https://cloud.feedly.com/v3/search/feeds/?query=nrk.no" for å søke etter feeds. Kan kjøre som Async.
        [ ] - Valider linken -> Sjekk om det er RSS v2 og om den har content. Eller bruk forskjellige funksjoner for hver (ATOM, RSS).
        [ ] - Display disse i en liste med Website - Title (hvor website er parset til bare domene)
        [ ] - Bruk switch toggle eller radio buttons til å velge hvilke saker som skal vises.
        [ ] - Hvis en toggle blir skrudd av -> vent til å slette denne fra oversikten og databasen til man går ut av view.
        [ ] - Disse skal vises i NAV view, hvor du kan trykke på en og bare nyheter fra denne vises.
        [ ] - Trykker man på ALL vises alle (fjern home og bytt til All).
        [ ] - Før RSS feeden lagres i DB: Sjekk om den allerede ligger der ved å sammenligne header og "fra" felt.
        */

        /* ToDo - Database:
        [ ] - Legg til et felt for å diferensiere mellom RSS feeds, må være unique (lagre RSS linken).
        [ ] - Lag funksjon som sammenliger HEADER, "fra felt" og "date".
        */

        /* ToDo - Service:
        [X] - Putt RSS parsing i en background service
        [ ] - La denne kjære automatisk ved et interval gitt i settings.
        */

        /* ToDo - Søk:
        [ ] - Få søk til å søke etter "noe" i header og summary -> åpne egen Activity for disse.
        */

        /* ToDo - Caching
        [ ] - Legg på caching for bilder og innhold fra RSS.
        */
    }

    // Broadcast receiver to receive from Service.
    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            News[] temp = mDB.getAllNewsFromDB();
            ArrayList<News> tempArrayList = new ArrayList<>(Arrays.asList(temp));

            mAdapter = new ContentAdapter(tempArrayList);       // Adds content to RecycleView.
            mRecyclerView.setAdapter(mAdapter);                 // Attaches the RecycleView.
        }
    };

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver,
                new IntentFilter(SERVICE_ACTION_RSS));
    }

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
        } else if (id == R.id.feeds) {
            Toast.makeText(this, "RSS feeds", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
