package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
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


public class MainActivity extends Activity implements OnShowcaseEventListener {

    // Views
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    // Instance variables
    private ArrayList<DrawerItem> appPages;
    private String titleMainApp;
    private String titlePage;
    private String titleCurrent;

    // Application
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the actionbar
        actionBar = getActionBar();

        // Get the SharedPrefs
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.PREFS), Context.MODE_PRIVATE);

        // Is it the first launch? If yes show the tutorial if not load the fragment like normal.
        if(sharedPref.getBoolean("firstLaunch", true)) {
            // We added the showcase library using Maven instead of having the actual file in the libs folder.
            // Create the showcase view to let the user know there is a drawer.
            new ShowcaseView.Builder(this)
                    .setTarget(new ActionViewTarget(this, ActionViewTarget.Type.TITLE))
                    .setContentTitle("Navigation")
                    .setContentText("Here is where the navigation drawer is located. TAP here or PULL from the side to access the navigation pages.")
                    .hideOnTouchOutside()
                    .setShowcaseEventListener(this)
                    .build();

            // No longer the first launch so change the status
            sharedPref.edit().putBoolean("firstLaunch", false).commit();
        } else {
            // In order to change fragments later you must add them dynamically (not via XML)
            // So here we setup which fragment we want to display first.
            setInitialFragment(new NewsFragment(), "Events");
        }

        // Set the pages for the app drawer and set the app title
        appPages = new ArrayList<DrawerItem>();
            appPages.add(new DrawerItem("Events", R.drawable.ic_fa_calendar_o));
            appPages.add(new DrawerItem("Student Schedule", R.drawable.ic_fa_calendar));
            appPages.add(new DrawerItem("Our Goals", R.drawable.ic_fa_graduation_cap));
            appPages.add(new DrawerItem("Admissions", R.drawable.ic_fa_envelope));
            appPages.add(new DrawerItem("Contact Us", R.drawable.ic_fa_fax));
        titlePage = appPages.get(0).getTitle();
        titleMainApp = "Sterling";

        // Get Views
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView)findViewById(R.id.lv_drawer_main);

        // Set the actionbar icon for the drawer icon
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Get the current page title and set it as the main_actions title
                titleCurrent = titlePage;
                actionBar.setTitle(titleCurrent);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                // Get the main_actions app title and set it as the main_actions title
                titleCurrent = titleMainApp;
                actionBar.setTitle(titleCurrent);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        // Show the action bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // Set adapters
        DrawerItemAdapter drawerItemAdapter = new DrawerItemAdapter(this, R.layout.drawer_item, appPages);
        drawerList.setAdapter(drawerItemAdapter);

        // Set listener for drawer items
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the main options menu, the one separate from any fragment options.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);
        return super.onCreateOptionsMenu(menu);
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
        if(item.getItemId() == R.id.action_settings ) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
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
                    // Create a new fragment and replace any existing fragment with it
                    Fragment scheduleFragment = new ScheduleFragment();

                    transaction.replace(R.id.fragment_container, scheduleFragment);
                    transaction.addToBackStack(null);
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
                    // Create a new fragment and replace any existing fragment with it
                    Fragment contactFragment = new ContactFragment();

                    transaction.replace(R.id.fragment_container, contactFragment);
                    transaction.addToBackStack(null);
                    break;
            }

            // Set the action bar title to the new page
            titlePage = appPages.get(position).getTitle();
            titleCurrent = titlePage;
            actionBar.setTitle(titleCurrent);

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
        transaction.replace(R.id.fragment_container, initialFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        actionBar.setTitle(initialTitle);
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        // The user closed the showcase view, let'supportMapFragment load the initial fragment now.
        setInitialFragment(new NewsFragment(), "Events");
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {
    }

}
