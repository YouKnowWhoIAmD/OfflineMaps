package com.junjunguo.pocketmaps.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.junjunguo.pocketmaps.R;
import com.junjunguo.pocketmaps.model.SportCategory;

import java.util.ArrayList;

/**
 * Created by Google Deveoper Student Club
 * Varshit Ratna(leader)
 * Devaraj Akhil(Core team)
 */

public class SpinnerAdapter extends ArrayAdapter<SportCategory> {
    private int resource;
    private ArrayList<SportCategory> list;
    LayoutInflater inflater;

    /**
     * @param context
     * @param resource single item layout to be inflated
     * @param list
     */
    public SpinnerAdapter(Context context, int resource, ArrayList<SportCategory> list) {
        super(context, resource, list);
        this.list = list;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return an item view with text + img
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = inflater.inflate(resource, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.analytics_activity_type_img);
        imageView.setImageResource(list.get(position).getImageId());
        TextView textView = (TextView) itemView.findViewById(R.id.analytics_activity_type_txt);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
