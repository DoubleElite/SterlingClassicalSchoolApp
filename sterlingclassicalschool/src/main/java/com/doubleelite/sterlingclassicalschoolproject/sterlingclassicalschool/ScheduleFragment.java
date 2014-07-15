package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.mobsandgeeks.adapters.Sectionizer;
import com.mobsandgeeks.adapters.SimpleSectionAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ScheduleFragment extends ListFragment implements ActionBar.OnNavigationListener {

    // Classes
    ArrayList<StudentClass> studentClasses;

    // Adapters
    StudentClassAdapter adapter;
    SimpleSectionAdapter<StudentClass> sectionAdapter;

    // Parsers
    StudentClassParser parser;

    // Application
    Context context;
    ActionBar actionBar;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the context from the main activity.
        context = getActivity();
        actionBar = getActivity().getActionBar();

        // Create a new parser
        parser = new StudentClassParser();

        // Set the result via an adapter and then set up the section adapter (custom lib).
        adapter = new StudentClassAdapter(context, R.layout.class_item, setClassScheduleForResult("schedule_12th.xml"));

        // Create a new SectionAdapter. We pass it our actual data adapter and then the layout and id of the view we use for the header.
        sectionAdapter = new SimpleSectionAdapter<StudentClass>(context, adapter, R.layout.list_header_item, R.id.tv_list_header, new StudentClassSectionizer());

        // Set the adapter for ListFragment we are using.
        setListAdapter(sectionAdapter);

        // Create the adapter for the action dropdown (select different grades)
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.schedule_grade_list,
                android.R.layout.simple_spinner_dropdown_item);

        // Show the action dropdown list and set the adapter
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, this);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        switch (position) {
            // 9th Grade \\
            case 0:
                setClassSchedule("schedule_9th.xml");
                break;
            // 10th Grade \\
            case 1:
                setClassSchedule("schedule_10th.xml");
                break;
            // 11th Grade \\
            case 2:
                setClassSchedule("schedule_11th.xml");
                break;
            // 12th Grade \\
            case 3:
                setClassSchedule("schedule_12th.xml");
                break;
        }
        return true;
    }


    public void setClassSchedule(String scheduleResourceName) {
        // Parse the xml file we passed through and set the contents (which returns an arraylist) to out studentclasses arraylist.
        try {
            // Remove the current schedule and then set it equal to the new one.
            studentClasses.clear();
            studentClasses = parser.parse(context.getAssets().open(scheduleResourceName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Make sure the adapter knows data has been changed (since the initial setting)
        sectionAdapter.notifyDataSetChanged();
    }

    public ArrayList<StudentClass> setClassScheduleForResult(String scheduleResourceName) {
        // Parse the xml file we passed through and set the contents (which returns an arraylist) to out studentclasses arraylist.
        try {
            return studentClasses = parser.parse(context.getAssets().open(scheduleResourceName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // onStop() is called when a fragment is invisible.
    @Override
    public void onStop() {
        super.onStop();
        // So we want to hide the action dropdown spinner since we only need in this fragment.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title. We do that here because if the user presses the back button
        // to get back to this fragment we need to update the title from the previous title.
        getActivity().getActionBar()
                .setTitle(R.string.fragment_title_schedule);
    }

    // Inner class for handling the sectionizer
    class StudentClassSectionizer implements Sectionizer<StudentClass> {

        @Override
        public String getSectionTitleForItem(StudentClass studentClass) {
            // Return the part of the item we want to be a heading
            return studentClass.day;
        }
    }


}
