package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.mobsandgeeks.adapters.Sectionizer;
import com.mobsandgeeks.adapters.SimpleSectionAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class ScheduleFragment extends ListFragment implements ActionBar.OnNavigationListener {

    ArrayList<StudentClass> studentClasses;
    StudentClassAdapter adapter;
    StudentClassParser parser;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create a new parse for parsing the different class schedules
        parser = new StudentClassParser();
        try {
            studentClasses = parser.parse(getActivity().getAssets().open("schedule_9th.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set the result via an adapter and then set up the section adapter (custom lib)
        adapter = new StudentClassAdapter(getActivity(), R.layout.class_item, studentClasses);

        SimpleSectionAdapter<StudentClass> sectionAdapter = new SimpleSectionAdapter<StudentClass>(getActivity(),
                adapter, R.layout.list_header_item, R.id.tv_list_header, new StudentClassSectionizer());

        setListAdapter(sectionAdapter);

        //getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.schedule_grade_list,
                android.R.layout.simple_spinner_dropdown_item);

        getActivity().getActionBar().setListNavigationCallbacks(spinnerAdapter, this);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        return false;
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
