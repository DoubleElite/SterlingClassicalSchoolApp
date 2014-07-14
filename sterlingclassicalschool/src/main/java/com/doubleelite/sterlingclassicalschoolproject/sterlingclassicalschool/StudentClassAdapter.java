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

public class StudentClassAdapter extends ArrayAdapter<StudentClass> {

    Context context;
    int layoutResourceId;
    ArrayList<StudentClass> item = null;

    public StudentClassAdapter(Context context, int layoutResourceId, ArrayList<StudentClass> item) {
        super(context, layoutResourceId, item);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.item = item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        StudentClassHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StudentClassHolder();
            holder.txtRoom = (TextView)row.findViewById(R.id.tv_class_room);
            holder.txtSubject = (TextView)row.findViewById(R.id.tv_class_subject);
            holder.txtTime = (TextView)row.findViewById(R.id.tv_class_time);

            row.setTag(holder);
        } else {
            holder = (StudentClassHolder)row.getTag();
        }

        StudentClass studentClass = item.get(position);
        holder.txtRoom.setText(studentClass.room);
        holder.txtSubject.setText(studentClass.subject);
        holder.txtTime.setText(studentClass.time);

        return row;
    }

    static class StudentClassHolder
    {
        TextView txtGrade;
        TextView txtSubject;
        TextView txtTime;
        TextView txtRoom;
        TextView txtColor;
    }
}
