package com.example.mobile_lab2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RssFeeds extends AppCompatActivity {
    public static final String SHARED_PREFS_RSS = "SHARED_PREFS_RSS";           // Const for the name of shared preferences.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feeds);
        setTitle("RSS feeds");

        EditText rssInput = findViewById(R.id.edit_rss_insert);
        ListView rssList = findViewById(R.id.list_rss_feeds);

        ArrayList<RssFeedEntry> rssFeeds = new ArrayList<>();                   // Holds rss objects.
        ArrayList<String> rssContent = new ArrayList<>();                       // Holds rss strings to be shown in list.

        // ToDo: Get RssFeedEntry from shared prefs.
        // ToDo: Create objects from them.

        // Create som RSS feeds.
        rssFeeds.add(
                new RssFeedEntry("https://www.nrk.no/toppsaker.rss",
                        false));
        rssFeeds.add(
                new RssFeedEntry("https://www.vg.no/rss/feed/?limit=10&format=rss&private=1&submit=Abonn%C3%A9r+n%C3%A5%21",
                        false));

        // Convert to string array and connect to adapter.
        for (int i = 0; i < rssFeeds.size(); i++) {
            rssContent.add(rssFeeds.get(i).getUrl());
        }
        

        // Listen for input on the editable text (rss url).
        rssInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {                       // If "done" is pressed in keyboard.

                rssFeeds.add(new RssFeedEntry(                                  // Adds a new RSS object to the array.
                        rssInput.getText().toString(), false));
                rssContent.add(rssInput.getText().toString());                  // Adds the url to the view.

                // ToDo: Save to shared prefs.

                rssInput.setText("");                                           // Removes text in "edit text" field.

                InputMethodManager imm =                                        // Remove keyboard.
                        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });


        // Creates the array adapter and connects it to the rss list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RssFeeds.this,
                android.R.layout.simple_list_item_1, rssContent);
        rssList.setAdapter(adapter);


        // On short click listener for item clicked on.
        rssList.setOnItemClickListener((parent, view, position, id) -> {        // Mark as active or not active.
            if (rssFeeds.get(position).getIsMarked()) {                         // Change color to illustrate who is active.
                rssFeeds.get(position).setIsMarked(false);
                parent.getChildAt(position).setBackgroundColor(
                        Color.parseColor("#D2D2D2"));
            } else {
                rssFeeds.get(position).setIsMarked(true);
                parent.getChildAt(position).setBackgroundColor(
                        Color.parseColor("#bdbdbd"));
            }

            // ToDo: Update shared preferences.

        });


        // On long click listener for for item clicked on.
        rssList.setOnItemLongClickListener((parent, view, position, id) -> {

            // ToDo: Delete from shared preferences.


            rssFeeds.remove(position);                                          // Delete from arrays.
            rssContent.remove(position);
            rssList.setAdapter(adapter);                                        // Updates adapter.

            return true;
        });


    }
}
