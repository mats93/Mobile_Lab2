package com.example.mobile_lab2;

import android.content.Context;
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

import java.util.ArrayList;

public class RssFeeds extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feeds);


        ListView rssList = findViewById(R.id.list_rss_feeds);

        // Create som temp list.
        ArrayList<String> temp = new ArrayList<>();

        // Get input from db? and from typed
        EditText rssInput = findViewById(R.id.edit_rss_insert);


        // Listen for input on the editable text (rss url).
        rssInput.setOnEditorActionListener((v, actionId, event) -> {
            // If "done" is pressed in keyboard.
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // Temp
                Toast.makeText(RssFeeds.this, rssInput.getText(), Toast.LENGTH_SHORT).show();
                temp.add(rssInput.getText().toString());
                rssInput.setText("");

                // Remove keyboard.
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        // Creates the array adapter and connects it to the rss list.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RssFeeds.this,
                android.R.layout.simple_list_item_1, temp);
        rssList.setAdapter(adapter);

        // On short click listener for item clicked on.
        rssList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RssFeeds.this,
                        "Picked " + rssList.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // On long click listener for for item clicked on.
        rssList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RssFeeds.this,
                        "Delete " + rssList.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }
}
