package com.example.mobile_lab2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class userPreferencesActivity extends AppCompatActivity {
    public static final String SHARED_PREFS_SETTINGS = "SHARED_PREFS_SETTINGS";   // Const for the name of shared preferences.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);
        setTitle("Settings");

        SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        Spinner numberOfNewsDisplay = findViewById(R.id.spinner_NoOfNews);
        Spinner syncIntervalNews = findViewById(R.id.spinner_SyncInterval);
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

        // Read state from shared preferences.
        numberOfNewsDisplay.setSelection(sharedpreferences.getInt("numberOfNewsToShow",0));
        syncIntervalNews.setSelection(sharedpreferences.getInt("syncIntervalNews", 0));

        // Button listener.
        btnApplyChanges.setOnClickListener(v -> {
            // Save state to shared preferences.
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("numberOfNewsToShow", numberOfNewsDisplay.getSelectedItemPosition());
            editor.putInt("syncIntervalNews", syncIntervalNews.getSelectedItemPosition());
            editor.apply();

            // Display toast.
            Toast toast = Toast.makeText(userPreferencesActivity.this,
                    "changes applied successfully", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }
}
