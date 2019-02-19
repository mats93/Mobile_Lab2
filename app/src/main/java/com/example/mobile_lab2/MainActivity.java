package com.example.mobile_lab2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;                                                         // The RecycleView.
    private RecyclerView.Adapter mAdapter;                                                      // Adapter to the RecycleView.
    private RecyclerView.LayoutManager mLayoutManager;                                          // Layout manager for RecycleView.
    private SearchView mSearchView;                                                             // Search bar.

    private String mTempDate;                                                                   // Date from xml, is inserted into "News" obj.
    private String mTempHeader;                                                                 // Title from xml, is inserted into "News" obj.
    private String mTempSummary;                                                                // Description from xml, is inserted into "News" obj.
    private String mTempImage;                                                                  // Image from xml, is inserted into "News" obj.
    private String mTempLink;                                                                   // URL link from xml, is inserted into "News" obj.

    // Database stuff....
    private DatabaseWrapper mDB;

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
        mDB.DropTable();

        //mDB.InsertToDB(new News("date", "Hello from DB",
        //       "This is a summary", "vg.no", "https://gfx.nrk.no/FPWgzpkxcBY29jwH7TIlgAJpWCcsl8C8Rd62b-jRZOsA"));

        //newsData.add(temp);

        //mAdapter = new ContentAdapter(newsData);
        //mRecyclerView.setAdapter(mAdapter);

        // DB testing....
        News[] temp = mDB.getAllNewsFromDB();
        ArrayList<News> tempArrayList = new ArrayList<>(Arrays.asList(temp));
        mAdapter = new ContentAdapter(tempArrayList);
        mRecyclerView.setAdapter(mAdapter);


        /* ToDo - RSS feed lagring og visning:
        [X] - Hent inn alle saker fra databasen.
        [ ] - Kjør fetch for hver feed.
        [ ] - Hvis fetch nyhet sin dato >= siste element i databasen -> legg den til (Gjør dette for hver nye sak)
        [ ] - Slett alle database elementer hvor dato < siste fetchede nyhets dato.
        [ ] - Display nye saker
        [ ] - Endre farge på de nyhetene som er klikket på.
        [ ] - Long click -> slett nyheten fra DB og view.
        */

        /* ToDo - RSS feed adding og sletting:
        [ ] - Bruk API "https://cloud.feedly.com/v3/search/feeds/?query=nrk.no" for å søke etter feeds. Kan kjøre som Async.
        [ ] - Valider linken -> Sjekk om det er RSS v2 og om den har content.
        [ ] - Display disse i en liste med Website - Title (hvor website er parset til bare domene)
        [ ] - Bruk switch toggle eller radio buttons til å velge hvilke saker som skal vises.
        [ ] - Hvis en toggle blir skrudd av -> vent til å slette denne fra oversikten og databasen til man går ut av view.
        [ ] - Disse skal vises i NAV view, hvor du kan trykke på en og bare nyheter fra denne vises.
        [ ] - Trykker man på ALL vises alle (fjern home og bytt til All).
        */

        /* ToDo - Database:
        [ ] - Legg til et felt for å diferensiere mellom RSS feeds, må være unique (lagre RSS linken).
        [ ] - Få date til å fungere ved sammenligninger.
        */

        /* ToDo - Service:
        [ ] - Putt RSS parsing i en background service
        [ ] - La denne kjære automatisk ved et interval gitt i settings.
         */

        /* ToDo - Søk:
        [ ] - Få søk til å søke etter "noe" i header og summary -> åpne egen Activity for disse.
        */

        new ProcessInBackground().execute();

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
        } else if (id == R.id.help) {
            Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // ToDo: Change to service. Should run every x. min (as specified in preferences) and if DB is empty.
    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception> {              // Async background task.

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);           // Starts progress text.
        Exception exception = null;                                                             // Returns exception or null.

        @Override
        protected void onPreExecute() {                                                         // Sets progress dialog before execution.
            super.onPreExecute();

            progressDialog.setMessage("Busy loading news feed...");
            progressDialog.show();
        }


        // ToDo: Only fetch items newer then <latest date in database> for each <RSS feed>.
        @Override
        protected Exception doInBackground(Integer... params) {                                 // XML parser in background.
            try {
                URL url = new URL("https://www.vg.no/rss/feed/?limit=10&format=rss&private=1&submit=Abonn%C3%A9r+n%C3%A5%21");                     // The RSS v.2 url to parse.
                // https://www.nrk.no/toppsaker.rss
                // https://www.vg.no/rss/feed/?limit=10&format=rss&private=1&submit=Abonn%C3%A9r+n%C3%A5%21

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();              // Creates a new PullParser factory.
                factory.setNamespaceAware(false);                                               // No support for XML namespaces.
                XmlPullParser xpp = factory.newPullParser();                                    // Starts the parser.
                xpp.setInput(url.openConnection().getInputStream(), "UTF_8");        // Opens up the URL connection.

                boolean insideItem = false;                                                     // false if not inside an "item" tag in xml.
                int eventType = xpp.getEventType();                                             // Gets the event type for the XML file.

                while(eventType != XmlPullParser.END_DOCUMENT) {                                // Loop until parser has reached end of XML document:
                    if (eventType == XmlPullParser.START_TAG) {                                 // If the parser is at an start tag in XML:
                        if (xpp.getName().equals("item")) {                                     // If the parser is inside the "item" tag:
                            insideItem = true;

                        } else if(xpp.getName().equalsIgnoreCase("title")) {         // Gets the title/header form xml.
                            if (insideItem) {
                                mTempHeader = xpp.nextText();
                            }
                        } else if(xpp.getName().equalsIgnoreCase("description")) {   // Gets the description/summary form xml.
                            if (insideItem) {
                                mTempSummary = xpp.nextText();
                            }
                        } else if (xpp.getName().equalsIgnoreCase("enclosure")) {    // Gets the image url from xml.
                            if (insideItem) {
                                mTempImage = xpp.getAttributeValue(null, "url");
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {         // Gets the link to the news article from xml.
                            if (insideItem) {
                                mTempLink = xpp.nextText();
                            }
                        } else if(xpp.getName().equalsIgnoreCase("pubDate")) {       // Gets the published date from the xml.
                            if (insideItem) {
                                mTempDate = xpp.nextText();

                                // ToDo: Insert into database. NewsDate should be mTempDate...
                                mDB.InsertToDB(new News(mTempDate,
                                        mTempHeader, mTempSummary, mTempLink, mTempImage));

                                mTempImage = "";                                                // RSS 2 does not have to include an image or date.
                                mTempDate = "";                                                 // Removes old entries in case of empty tags for next item.
                            }
                        }
                    } else if (eventType == XmlPullParser.END_TAG &&                            // If parser has reached the end of the document
                            xpp.getName().equalsIgnoreCase("item")) {                //  and is outside of "item" tag:
                        insideItem = false;                                                     // Parser is no longer inside an "item" tag, should not read data.
                    }
                    eventType = xpp.next();                                                     // Gets the next line in the xml document.
                }
            } catch (MalformedURLException ex) {
                exception = ex;
            } catch (XmlPullParserException ex) {
                exception = ex;
            } catch (IOException ex) {
                exception = ex;
            }
            return exception;                                                                   // Returns null or exception.
        }

        @Override
        protected void onPostExecute(Exception s) {                                             // After execution of background task:
            super.onPostExecute(s);
           // mAdapter = new ContentAdapter(newsData);                                            // Creates a new adapter to RecycleView with the new data.
            mRecyclerView.setAdapter(mAdapter);                                                 // Connects to the adapter to display new data.

            // DB testing....
            News[] temp = mDB.getAllNewsFromDB();

            ArrayList<News> tempArrayList = new ArrayList<>(Arrays.asList(temp));

            mAdapter = new ContentAdapter(tempArrayList);
            mRecyclerView.setAdapter(mAdapter);

            progressDialog.dismiss();
        }
    }
}
