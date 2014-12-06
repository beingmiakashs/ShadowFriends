package com.omelet.shadowdriends.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.model.PickerItem;
 
public class PickersListAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<PickerItem> data;
    private static LayoutInflater inflater=null;
 
    public PickersListAdapter(Activity activity, ArrayList<PickerItem> data) {
        activity = activity;
        this.data = data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return data.get(position);
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row_other, null);
 
        TextView title = (TextView)vi.findViewById(R.id.title);
        TextView artist = (TextView)vi.findViewById(R.id.artist);
        TextView distance = (TextView)vi.findViewById(R.id.distance);
 
        PickerItem pickerItem = data.get(position);
 
        title.setText(pickerItem.getFirstName()+" "+pickerItem.getLastName());
        artist.setText(pickerItem.getMobileNumber());
        distance.setText("Total Pack :"+String.valueOf(pickerItem.getTotal()));
        return vi;
    }
}