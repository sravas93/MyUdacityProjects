package com.example.sravanthy.habittrackerapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by sandeep on 12/15/2016.
 */
public class TrackerCursorAdapter extends CursorAdapter {

    public TrackerCursorAdapter(Context context, Cursor c) {

        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
    return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView Name = (TextView) view.findViewById(R.id.Name);
        TextView Practice_Hours =(TextView) view.findViewById(R.id.Practice_hours);


    }
}
