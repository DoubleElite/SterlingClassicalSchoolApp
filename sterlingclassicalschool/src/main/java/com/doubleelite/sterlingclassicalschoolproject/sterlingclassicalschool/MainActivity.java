package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
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
import java.util.Collections;


public class MainActivity extends Activity {

    // Views
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    // Instance variables
    private String[] appPages;
    private String titleMainApp;
    private String titlePage;
    private String titleCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In order to change fragments later you must add them dynamically (not via XML)
        // So here we setup which fragment we want to display first.
        setInitialFragment(new NewsFragment(), "Events");

        // Set the pages for the app drawer and set the app title
        appPages = getResources().getStringArray(R.array.main_app_drawer_pages);
        titlePage = appPages[0];
        titleMainApp = "Sterling";

        // Get Views
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.lv_drawer_main);

        // Set the actionbar icon for the drawer icon
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Get the current page title and set it as the news_actions title
                titleCurrent = titlePage;
                getActionBar().setTitle(titleCurrent);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                // Get the news_actions app title and set it as the news_actions title
                titleCurrent = titleMainApp;
                getActionBar().setTitle(titleCurrent);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        // Show the action bar icon
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set adapters
        DrawerItemAdapter drawerItemAdapter = new DrawerItemAdapter(this, R.layout.drawer_item, appPages);
        drawerList.setAdapter(drawerItemAdapter);

        // Set listener for drawer items
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // Get the FragmentTransaction so we can replace/add fragments
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            switch (position) {
                case 0:
                    // Events \\
                    // Create a new fragment and replace any existing fragment with it
                    Fragment newsFragment = new NewsFragment();

                    transaction.replace(R.id.fragment_container, newsFragment);
                    transaction.addToBackStack(null);
                    break;

                case 1:
                    // Student Schedule \\

                    break;

                case 2:
                    // Our Goals (Information) \\
                    // Create a new fragment and replace any existing fragment with it
                    Fragment informationFragment = new InformationFragment();

                    transaction.replace(R.id.fragment_container, informationFragment);
                    transaction.addToBackStack(null);
                    break;

                case 3:
                    // Admissions \\
                    // Create a new fragment and replace any existing fragment with it
                    Fragment admissionsFragment = new AdmissionsFragment();

                    transaction.replace(R.id.fragment_container, admissionsFragment);
                    transaction.addToBackStack(null);
                    break;

                case 4:
                    // Contact Us \\

                    break;
            }

            // Set the action bar title to the new page
            titlePage = appPages[position];
            titleCurrent = titlePage;
            getActionBar().setTitle(titleCurrent);

            // Finish the transaction process and show the new fragment with a transition
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.commit();

            // Highlight the selected item and close the drawer
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);
        }
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view.
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        // Wrap in a try catch because if one of the items is removed by removing a fragment
        // Then it will throw an error and crash.
        try {
            menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void setInitialFragment(Fragment fragment, String initialTitle) {
        // Create the fragment from the constructor and add it to the fragment container ViewGroup
        Fragment initialFragment = fragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, initialFragment);
        transaction.commit();
        getActionBar().setTitle(initialTitle);
    }

}
