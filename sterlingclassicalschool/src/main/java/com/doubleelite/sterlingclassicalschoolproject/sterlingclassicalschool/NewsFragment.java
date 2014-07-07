package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class NewsFragment extends Fragment {

    // Views
    ListView lvNewsItems;
    RelativeLayout loadingLayout;

    // Adapters
    NewsItemAdapter newsItemAdapter;

    // Classes
    ArrayList<NewsItem> newsItems;
    NewsItem item;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing classes
        newsItems = new ArrayList<NewsItem>();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the fragment view that we'll return after we get the other views from it
        View v = inflater.inflate(R.layout.news_fragment, container, false);

        // Get news item
        lvNewsItems = (ListView)v.findViewById(R.id.lv_newsItems);
        loadingLayout = (RelativeLayout)v.findViewById(R.id.loading_layout_news_fragment);

        try {
            URL url = new URL("http://feeds.feedburner.com/SterlingClassicalSchool?format=xml");
            new URLAsyncTask().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return v;
    }

    private class URLAsyncTask extends AsyncTask<URL, String, ArrayList<NewsItem>> {
        // Use the URL passed in the AysncClass and return an InputStream to be used in onPostExecute
        @Override
        protected ArrayList<NewsItem> doInBackground(URL... params) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // We will get the XML from an input stream
                xpp.setInput(params[0].openConnection().getInputStream(), "UTF_8");

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
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                // Get the description. Use Jsoup to parse out all the HTML in it.
                                // Then take everything before the event date part (which is then just the description itself)
                                String htmlDescription = xpp.nextText();
                                String description = Jsoup.parse(htmlDescription).text();
                                description = description.substring(0, description.indexOf("event date"));
                                item.description = description;
                            }
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                // Get the text from XML. Take the date which is formatted like Tues, 27 Aug 2013 05:00:00GMT
                                // And  only take the text before the first 0 - Tues, 27 Aug
                                // Lookup String.split() for more info on how it works
                                String date = xpp.nextText();
                                String[] dateSplit = date.split("05:");
                                item.date = dateSplit[0];
                            }
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
            // This is the only way I know of showing the most recently added items in the calender at the top
            // If this wasn't here the newest items would be at the bottom
            Collections.reverse(newsItems);
            return newsItems;
        }

        @Override
        protected void onPostExecute(ArrayList<NewsItem> newsItem) {
            Log.v("APP", "Done downloading now parse it");
            newsItemAdapter = new NewsItemAdapter(context, R.layout.news_item, newsItem);
            lvNewsItems.setAdapter(newsItemAdapter);

            // Remove the loading icon and relative layout it is nestled in
            loadingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // This must be called to have the option menu show up
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu resource we want to use
        inflater.inflate(R.menu.main, menu);
    }
}
