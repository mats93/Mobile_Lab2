package com.example.mobile_lab2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class articleViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);

        String newsLink = getIntent().                                          // Gets the link that was passed from
                getStringExtra(ContentAdapter.ContentViewHolder.NEWS_ARTICLE);  //  'ContentAdapter.ContentViewHolder'.

        WebView articleWebView = findViewById(R.id.articleWebView);             // Gets the web view from xml.
        articleWebView.setWebViewClient(new WebViewClient());                   // Starts a new web client inside the app.
        articleWebView.loadUrl(newsLink);                                       // Displays the article inside the WebView.
    }
}
