package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    Context context;
    int layoutResourceId;
    ArrayList<NewsItem> item = null;

    public NewsItemAdapter(Context context, int layoutResourceId, ArrayList<NewsItem> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.item = item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        // Class to cache the views
        NewsItemHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new NewsItemHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.tv_title);
            holder.txtDate = (TextView)row.findViewById(R.id.tv_date);
            holder.txtDescription = (TextView)row.findViewById(R.id.tv_description);

            row.setTag(holder);
        }
        else
        {
            holder = (NewsItemHolder)row.getTag();
        }

        // Create a new NewsItem object and set it equal the NewsItem that is being passed through.
        // I could just replace all instances of "newsItem.*variablehere*" with "item[position].*variablehere*" instead.
        // But this method has better performance.
        NewsItem newsItem = item.get(position);
        holder.txtTitle.setText(newsItem.title);
        holder.txtDate.setText(newsItem.date);
        holder.txtDescription.setText(newsItem.description);

        return row;
    }

    // This class caches the textviews so we don't have to find them each time.

    static class NewsItemHolder
    {
        TextView txtTitle;
        TextView txtDescription;
        TextView txtDate;
    }
}
