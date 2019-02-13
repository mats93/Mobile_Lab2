package com.example.mobile_lab2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class userPreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_user_preferences);
    }
}
