package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class DrawerItemAdapter extends ArrayAdapter<String> {

    public DrawerItemAdapter(Context context, int layoutResourceId, ArrayList<String> item) {
        super(context, layoutResourceId, item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
