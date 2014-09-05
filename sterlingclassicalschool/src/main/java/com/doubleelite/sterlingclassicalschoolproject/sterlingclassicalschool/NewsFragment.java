package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class NewsFragment extends Fragment {

    // Views
    private ListView lvNewsItems;
    private RelativeLayout loadingLayout;

    // Adapters
    private NewsItemAdapter newsItemAdapter;

    // Classes
    private ArrayList<NewsItem> newsItems;
    private NewsItem item;
    private Context context;

    // Instance
    private Boolean isDownloading = false;

    // Files
    InputStream eventXmlFIle;
    File cacheFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing classes
        newsItems = new ArrayList<NewsItem>();
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the fragment view that we'll return after we get the other views from it.
        View v = inflater.inflate(R.layout.news_fragment, container, false);

        // Get news item.
        lvNewsItems = (ListView)v.findViewById(R.id.lv_newsItems);
        loadingLayout = (RelativeLayout)v.findViewById(R.id.loading_layout_news_fragment);

        // Hide any actionbar spinners that be left over from other fragments that didn't close properly.
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        // Get SharedPrefs and the SharedPrefs Editor.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.PREFS), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Initial download. Once we do this once we only want the user to be able to update it.
        if(sharedPreferences.getBoolean("firstTimeRunningApp", true)) {
            downloadNewsItems();
            // We now have ran the app for the first time. Put the value in and apply it.
            editor.putBoolean("firstTimeRunningApp", false);
            editor.apply();
        } else {
            // After initial launch do this instead. It finds the local file instead of downloading it.
            setNewsItems();
        }

        return v;
    }

    private class DownloadNewsItemTask extends AsyncTask<URL, String, ArrayList<NewsItem>> {
        // Use the URL passed in the AysncClass and return an InputStream to be used in onPostExecute
        @Override
        protected ArrayList<NewsItem> doInBackground(URL... params) {
            try {
                URL newsXmlFile = params[0];
                URLConnection connection = newsXmlFile.openConnection();
                InputStream inputStream = new BufferedInputStream(newsXmlFile.openStream(), 10240);
                // Set the path where we want to save the file
                // in this case, going to save it on the root directory of the
                // sd card.
                File SDCardRoot = Environment.getExternalStorageDirectory();
                // Create a new file, specifying the path, and the filename
                // which we want to save the file as.
                cacheFile = new File(SDCardRoot, "events.xml");
                FileOutputStream outputStream = new FileOutputStream(cacheFile);

                byte buffer[] = new byte[1024];
                int dataSize;
                while ((dataSize = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, dataSize);
                }

                outputStream.close();

                return parseXml(cacheFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        // This method has access to the UI and is ran after everything is done in the background
        @Override
        protected void onPostExecute(ArrayList<NewsItem> newsItems) {
            updateNewsListView(newsItems);
            // Was there any connection? If not nothing in the background task would work
            // So it would skip right to onPostExecute. And we'll tell the user that there is no connection.
            // We also have to do this here because in order to access the Toast class you need to be on the UI Thread.
            checkWorkingNetworkStatus();
            isDownloading = false;
        }
    }

    private class ParseExistingNewsItemTask extends AsyncTask<File, Void, ArrayList<NewsItem>> {
        // Use the URL passed in the AysncClass and return an InputStream to be used in onPostExecute
        @Override
        protected ArrayList<NewsItem> doInBackground(File... params) {
            // Get an arraylist from the parse method which parse a given xml file.
            // In this case we use the one we passed through the constructor.
            return parseXml(params[0]);
        }
        // This method has access to the UI and is ran after everything is done in the background
        @Override
        protected void onPostExecute(ArrayList<NewsItem> newsItems) {
            updateNewsListView(newsItems);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // This must be called to have the option menu show up.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Clear the menu before adding new items. Otherwise there is a chance you will have duplicate items.
        menu.clear();
        // Inflate the menu resource we want to use.
        inflater.inflate(R.menu.news_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh) {
            downloadNewsItems();
            Toast.makeText(context, "Refreshing Data", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title. We do that here because if the user presses the back button
        // to get back to this fragment we need to update the title from the previous title.
        getActivity().getActionBar().setTitle(R.string.fragment_title_events);
    }

    // This method just checks if there is a connection to use and returns true if there is.
    public boolean checkWorkingNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        // Show a toast that there is no connection so the user knows.
        Toast.makeText(context, "No internet connection. Try again later.", Toast.LENGTH_LONG).show();
        return false;
    }

    private void downloadNewsItems() {
        // If we aren't already downloading anything go ahead and get the items again.
        if (!isDownloading) {
            try {
                URL url = new URL("http://feeds.feedburner.com/SterlingClassicalSchool?format=xml");
                new DownloadNewsItemTask().execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        isDownloading = true;
    }

    private void setNewsItems() {
        // Set the path where we want to save the file
        // in this case, going to save it on the root directory of the
        // sd card.
        File SDCardRoot = Environment.getExternalStorageDirectory();
        // Create a new file, specifying the path, and the filename
        // which we want to save the file as.
        cacheFile = new File(SDCardRoot, "events.xml");
        // Parse the file on a separate thread.
        new ParseExistingNewsItemTask().execute(cacheFile);
    }

    // This method is only ran from AsyncTask. Parses a given xml file.
    public ArrayList<NewsItem> parseXml(File cacheFile) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            // Get the actual XML file as an InputStream
            eventXmlFIle = new FileInputStream(cacheFile);

            // We will get the XML from an input stream
            xpp.setInput(eventXmlFIle, "UTF_8");

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
                    // Check to see if the event has happened yet, if it has don't show it.
                    try {
                        Date dateOfItem = new SimpleDateFormat("E, d MMMM yyyy", Locale.ENGLISH).parse(item.date);
                        // TODO: IF dateOfItem is >= todays date then add the item
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

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
        // If this wasn't here the newest items would be at the bottom.
        // Collections.reverse(newsItems);
        return newsItems;
    }

    private void updateNewsListView(ArrayList<NewsItem> newsItems) {
        newsItemAdapter = new NewsItemAdapter(context, R.layout.news_item, newsItems);
        newsItemAdapter.notifyDataSetChanged();

        // Create the ListView animation adapter from the listviewanimations lib,
        // Then we pass in our actual data adapter.
        SwingRightInAnimationAdapter swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(newsItemAdapter);

        // Assign the ListView to the AnimationAdapter and vice versa.
        swingRightInAnimationAdapter.setAbsListView(lvNewsItems);
        lvNewsItems.setAdapter(swingRightInAnimationAdapter);

        // Remove the loading icon and relative layout it is nestled in.
        loadingLayout.setVisibility(View.GONE);
    }

}
