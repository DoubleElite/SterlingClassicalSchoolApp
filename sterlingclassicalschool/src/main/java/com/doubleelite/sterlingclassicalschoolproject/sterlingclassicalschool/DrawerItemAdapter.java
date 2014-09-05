package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DrawerItemAdapter extends ArrayAdapter<DrawerItem> {

    private ArrayList<DrawerItem> item;
    private Context context;
    private int layoutResourceId;


    public DrawerItemAdapter(Context context, int layoutResourceId, ArrayList<DrawerItem> item) {
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
        ImageView icon = (ImageView)view.findViewById(R.id.iv_drawer_icon);

        title.setText(item.get(position).getTitle());
        icon.setImageResource(item.get(position).getIcon());

        return view;
    }
}
