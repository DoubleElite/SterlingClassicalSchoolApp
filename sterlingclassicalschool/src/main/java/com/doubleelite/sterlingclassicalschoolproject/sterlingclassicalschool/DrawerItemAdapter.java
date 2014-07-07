package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DrawerItemAdapter extends ArrayAdapter<String> {

    private String[] item;
    private int layoutResourceId;
    private Context context;


    public DrawerItemAdapter(Context context, int layoutResourceId, String[] item) {
        super(context, layoutResourceId, item);
        this.item = item;
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(layoutResourceId, parent, false);

        TextView title = (TextView)view.findViewById(R.id.tv_drawer_item);

        title.setText(item[position]);

        return view;
    }
}
