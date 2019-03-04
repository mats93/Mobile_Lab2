package com.example.mobile_lab2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class userPreferencesActivity extends AppCompatActivity {
    public static final String SHARED_PREFS_SETTINGS = "SHARED_PREFS_SETTINGS";     // Const for settings shared preferences.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
        setTitle("Settings");

        SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        Spinner numberOfNewsDisplay = findViewById(R.id.spinner_NoOfNews);
        Spinner syncIntervalNews = findViewById(R.id.spinner_SyncInterval);
        EditText rssInput = findViewById(R.id.edit_rss_insert);
        TextView rssShow = findViewById(R.id.text_show_rss);
        Button btnApplyChanges = findViewById(R.id.btn_apply);

        // Array to populate the spinners.
        ArrayList<Integer> newsDisplayItems = new ArrayList<>();
        ArrayList<String> syncItems = new ArrayList<>();

        // Add the values to the array.
        newsDisplayItems.add(10);
        newsDisplayItems.add(20);
        newsDisplayItems.add(50);
        newsDisplayItems.add(70);
        newsDisplayItems.add(100);

        syncItems.add("10 min");
        syncItems.add("30 min");
        syncItems.add("1 hour");
        syncItems.add("5 hours");
        syncItems.add("12 hours");
        syncItems.add("24 hours");

        // Example of RSS feeds:
        // https://www.nrk.no/toppsaker.rss
        // https://www.vg.no/rss/feed/?limit=10&format=rss&private=1&submit=Abonn%C3%A9r+n%C3%A5%21

        // Connect to spinner for "numberOfNewsDisplay".
        ArrayAdapter<Integer> newsDisplayAdapter = new ArrayAdapter<>(userPreferencesActivity.this,
                android.R.layout.simple_list_item_1, newsDisplayItems);
        newsDisplayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfNewsDisplay.setAdapter(newsDisplayAdapter);

        // Connects to spinner for "syncIntervalNews".
        ArrayAdapter<String> syncItemsAdapter = new ArrayAdapter<>(userPreferencesActivity.this,
                android.R.layout.simple_list_item_1, syncItems);
        syncItemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        syncIntervalNews.setAdapter(syncItemsAdapter);

        // Read settings from shared preferences.
        int numberOfNews = sharedpreferences.getInt("numberOfNewsToShow",0);
        String syncInterval = sharedpreferences.getString("syncIntervalNews", "");

        // Sett the spinner to display the selected item from shared preferences.
        numberOfNewsDisplay.setSelection(newsDisplayItems.indexOf(numberOfNews));
        syncIntervalNews.setSelection(syncItems.indexOf(syncInterval));

        rssShow.setText(sharedpreferences.getString("rss", ""));    // Read rss from shared preferences.

        // Listen for input on the editable text (rss url).
        rssInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {                       // If "done" is pressed in keyboard.

                rssShow.setText(rssInput.getText().toString());                 // Set content of text view.
                rssInput.setText("");                                           // Removes text in "edit text" field.

                InputMethodManager imm =                                        // Remove keyboard.
                        (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        // Button listener.
        btnApplyChanges.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedpreferences.edit();         // Save state to shared preferences.

            editor.putInt("numberOfNewsToShow",
                    Integer.parseInt(numberOfNewsDisplay.getSelectedItem().toString()));

            editor.putString("syncIntervalNews",
                    syncIntervalNews.getSelectedItem().toString());

            editor.putString("rss", rssShow.getText().toString());
            editor.apply();                                                     // Applies the changes.


            Toast toast = Toast.makeText(userPreferencesActivity.this,
                    "changes applied successfully", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            MainActivity.startBackgroundService(this);                  // Starts the background service.
        });
    }
}
