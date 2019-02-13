package com.example.mobile_lab2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;                                                         //
    private RecyclerView.Adapter mAdapter;                                                      //
    private RecyclerView.LayoutManager mLayoutManager;                                          //

    private SearchView mSearchView;

    private ArrayList<News> newsData = new ArrayList<>();                                       // News data to be put into the RecycleView cards.
    private String tempDate;                                                                    // Date from xml, is inserted into "News" obj.
    private String tempHeader;                                                                  // Title from xml, is inserted into "News" obj.
    private String tempSummary;                                                                 // Description from xml, is inserted into "News" obj.
    private String tempImage;                                                                   // Image from xml, is inserted into "News" obj.
    private String tempLink;                                                                    // URL link from xml, is inserted into "News" obj.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Menu stuff....
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("News reader");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Recycle view stuff...
        mRecyclerView = findViewById(R.id.contentView);                                         // Gets the RecyclerView.
        mRecyclerView.setHasFixedSize(true);                                                    // Optimizing.

        mLayoutManager = new LinearLayoutManager(this);                                  //
        mRecyclerView.setLayoutManager(mLayoutManager);

        new ProcessInBackground().execute();
    }

    @Override
    public void onBackPressed() {                           // Handles back press on nav bar.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {         // Handles toolbar options (Search).
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        mSearchView = (SearchView) myActionMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
                if( ! mSearchView.isIconified()) {
                    mSearchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {    // Handles nav bar.
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

            progressDialog.setMessage("Busy loading rss feed...");
            progressDialog.show();
        }


        // ToDo: Only fetch items newer then <latest date in database> for each <RSS feed>.
        @Override
        protected Exception doInBackground(Integer... params) {                                 // XML parser in background.
            try {
                URL url = new URL("https://www.nrk.no/toppsaker.rss");                     // The RSS v.2 url to parse.
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
                                tempHeader = xpp.nextText();
                            }
                        } else if(xpp.getName().equalsIgnoreCase("description")) {   // Gets the description/summary form xml.
                            if (insideItem) {
                                tempSummary = xpp.nextText();
                            }
                        } else if (xpp.getName().equalsIgnoreCase("enclosure")) {    // Gets the image url from xml.
                            if (insideItem) {
                                tempImage = xpp.getAttributeValue(null, "url");
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {         // Gets the link to the news article from xml.
                            if (insideItem) {
                                tempLink = xpp.nextText();
                            }
                        } else if(xpp.getName().equalsIgnoreCase("pubDate")) {       // Gets the published date from the xml.
                            if (insideItem) {
                                tempDate = xpp.nextText();

                                // ToDo: Insert into database.
                                newsData.add(new News(tempDate,                                 // Adds the the News object to the ArrayList.
                                        tempHeader, tempSummary, tempLink, tempImage));

                                tempImage = "";                                                 // RSS 2 does not have to include an image or date.
                                tempDate = "";                                                  // Removes old entries in case of empty tags for next item.
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
            mAdapter = new ContentAdapter(newsData);                                            // Creates a new adapter to RecycleView with the new data.
            mRecyclerView.setAdapter(mAdapter);                                                 // Connects to the adapter to display new data.

            progressDialog.dismiss();
        }
    }
}
