package com.example.mobile_lab2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RSSPullService extends IntentService {
    private SharedPreferences mSharedpreferences;
    private String mTempDate;                                                                   // Date from xml, is inserted into "News" obj.
    private String mTempHeader;                                                                 // Title from xml, is inserted into "News" obj.
    private String mTempSummary;                                                                // Description from xml, is inserted into "News" obj.
    private String mTempImage;                                                                  // Image from xml, is inserted into "News" obj.
    private String mTempLink;                                                                   // URL link from xml, is inserted into "News" obj.
    private DatabaseWrapper mDB;

    public RSSPullService() {
        super("RSSPullService");
    }

    public RSSPullService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("RSS service", "RSS background service started");

        mSharedpreferences = getSharedPreferences(userPreferencesActivity.SHARED_PREFS_SETTINGS, Context.MODE_PRIVATE);
        mDB = new DatabaseWrapper(getBaseContext(), "news");
        String rssURL = mSharedpreferences.getString("rss", "");

        try {
            URL url = new URL(rssURL);
            ProcessRSSFeed(url);                                                            // Fetches the news from the RSS feed.
            mDB.deleteNewsOlderThen("3");                                               // Deletes news older then 3 days from DB.
            sendBroadcast();                                                                // Sends broadcast to MainActivity.

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d("RSS service", "RSS background service finished");
    }

    private void sendBroadcast() {                                                          // Sends broadcast to local broadcast receiver.
        Intent intent = new Intent(MainActivity.SERVICE_ACTION_RSS);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void ProcessRSSFeed(URL url) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();              // Creates a new PullParser factory.
            factory.setNamespaceAware(false);                                               // No support for XML namespaces.
            XmlPullParser xpp = factory.newPullParser();                                    // Starts the parser.
            xpp.setInput(url.openConnection().getInputStream(), "UTF_8");        // Opens up the URL connection.

            boolean insideItem = false;                                                     // false if not inside an "item" tag in xml.
            int eventType = xpp.getEventType();                                             // Gets the event type for the XML file.

            while(eventType != XmlPullParser.END_DOCUMENT ) {                               // Loop until parser has reached end of XML document:
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

                            if (!mDB.articleExistsAlready(mTempLink)) {                     // If the article already exists, don`t add it.
                                mDB.InsertToDB(new News(mTempDate, mTempHeader,
                                        mTempSummary, mTempLink, mTempImage));
                            }
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
            Log.d("RSS service", "ProcessRSSFeed: " + ex);
        } catch (XmlPullParserException ex) {
            Log.d("RSS service", "ProcessRSSFeed: " + ex);
        } catch (IOException ex) {
            Log.d("RSS service", "ProcessRSSFeed: " + ex);
        }
    }
}
