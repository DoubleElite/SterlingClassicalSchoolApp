package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {

    ListView lvNewsItems;
    NewsItemAdapter newsItemAdapter;
    ArrayList<NewsItem> newsItems;
    NewsItem item;
    InputStream urlInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvNewsItems = (ListView)findViewById(R.id.lv_newsItems);

        // Initializing instance variables
        newsItems = new ArrayList<NewsItem>();

        try {
            new URLAsyncTask().execute(new URL("http://feeds.feedburner.com/SterlingClassicalSchool?format=xml"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ParseXMLAndShowIt(InputStream stream) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            // We will get the XML from an input stream
            xpp.setInput(stream, "UTF_8");

            /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
             * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
             * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
             * so we should skip the "<title>" tag which is a child of "<channel>" tag,
             * and take in consideration only "<title>" tag which is a child of "<item>"
             *
             * In order to achieve this, we will make use of a boolean variable.
             */
            boolean insideItem = false;

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                        item = new NewsItem();
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem)
                            item.title = xpp.nextText();
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem) {}
                        //links.add(xpp.nextText()); //extract the link of article
                    }
                } else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                    insideItem=false;
                    newsItems.add(item);
                }
                eventType = xpp.next(); //move to next element
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        newsItemAdapter = new NewsItemAdapter(this, R.layout.news_item, newsItems);

        lvNewsItems.setAdapter(newsItemAdapter);

    }

    private class URLAsyncTask extends AsyncTask<URL, String, InputStream> {
        // Use the URL passed in the AysncClass and return an InputStream to be used in onPostExecute
        @Override
        protected InputStream doInBackground(URL... params) {
            try {
                Log.v("APP", "Downloading File");
                return params[0].openConnection().getInputStream();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            Log.v("APP", "Done downloading now parse it");
            ParseXMLAndShowIt(inputStream);
        }
    }

}
