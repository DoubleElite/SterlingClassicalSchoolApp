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

    ArrayList<StudentClass> studentClasses;

    StudentClassAdapter adapter;
    SimpleSectionAdapter<StudentClass> sectionAdapter;

    StudentClassParser parser;

    Context context;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the context from the main activity.
        context = getActivity();

        // Create a new parser
        parser = new StudentClassParser();

        // Set the result via an adapter and then set up the section adapter (custom lib).
        adapter = new StudentClassAdapter(context, R.layout.class_item, setClassScheduleForResult("schedule_12th.xml"));

        // Create a new SectionAdapter. We pass it our actual data adapter and then the layout and id of the view we use for the header.
        sectionAdapter = new SimpleSectionAdapter<StudentClass>(context, adapter, R.layout.list_header_item, R.id.tv_list_header, new StudentClassSectionizer());

        // Set the adapter for ListFragment we are using.
        setListAdapter(sectionAdapter);


        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.schedule_grade_list,
                android.R.layout.simple_spinner_dropdown_item);

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getActivity().getActionBar().setListNavigationCallbacks(spinnerAdapter, this);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        switch (position) {
            // 9th Grade \\
            case 0:
                //setClassSchedule("schedule_9th.xml");
                break;
            // 10th Grade \\
            case 1:

                break;
            // 11th Grade \\
            case 2:

                break;
            // 12th Grade \\
            case 3:

                break;
        }
        return true;
    }


    public void setClassSchedule(String scheduleResourceName) {
        // Parse the xml file we passed through and set the contents (which returns an arraylist) to out studentclasses arraylist.
        try {
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

    // Inner class for handling the sectionizer
    class StudentClassSectionizer implements Sectionizer<StudentClass> {

        @Override
        public String getSectionTitleForItem(StudentClass studentClass) {
            // Return the part of the item we want to be a heading
            return studentClass.day;
        }
    }


}
