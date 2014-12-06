package com.omelet.shadowdriends.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.omelet.sa.pickmypack.R;
import com.omelet.shadowdriends.model.PackItem;
import com.omelet.shadowfriends.util.GlobalConstant;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class PacksListAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<PackItem> data;
    private static LayoutInflater inflater=null;
 
    public PacksListAdapter(Activity activity, ArrayList<PackItem> data) {
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
 
        PackItem packItem = data.get(position);
 
        title.setText(packItem.getPackTitle());
        artist.setText(packItem.getPackDescription());
        if(packItem.getDistance()>=0)distance.setText(String.valueOf(packItem.getDistance())+"m");
        return vi;
    }
}