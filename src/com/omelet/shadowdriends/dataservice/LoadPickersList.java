package com.omelet.shadowdriends.dataservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.omelet.shadowdriends.adapter.PacksListAdapter;
import com.omelet.shadowdriends.adapter.PickersListAdapter;
import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.location.GlobalLocation;
import com.omelet.shadowdriends.model.PackItem;
import com.omelet.shadowdriends.model.PickerItem;
import com.omelet.shadowfriends.DetailsActivity;
import com.omelet.shadowfriends.util.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.ListView;

public class LoadPickersList extends AsyncTaskLoader<PickerListDataHandler> {

		PickerListDataHandler mHaDataHandler;
		
		Context mContext;
		private String urlToGetAllPacks;
		private Activity mActivity;
		private ListView mListView;
		
		private JSONParser jParser;
		private JSONArray packs = null;
		private ArrayList<PickerItem> packsList;
		private boolean isSuccess = false;
	    
		private static final String TAG_STATUS = "status";
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_PICKERS = "pickker";
		private static final String TAG_FIRST_NAME = "FIRST_NAME";
		private static final String TAG_LAST_NAME = "LAST_NAME";
		private static final String TAG_MOBILE_NUMBER = "MOBILE_NUMBER";
		private static final String TAG_TOTAL_PACK = "TOTAL";

		public LoadPickersList(Context context, Activity activity, ListView listView) {
			super(context);
			mContext = context;
			mActivity = activity;
			mListView = listView;
			mHaDataHandler = new PickerListDataHandler();
		}

		@Override
		public PickerListDataHandler loadInBackground() {
			
			loadPacksList();
			mHaDataHandler.setData(packsList);
			return mHaDataHandler;
		}

		@Override
		public void deliverResult(PickerListDataHandler data) {
			super.deliverResult(data);
			mListView.setAdapter(new PickersListAdapter(mActivity, data.getData()));
		}
		
		public void loadPacksList() {
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        jParser = new JSONParser(mContext);
	        JSONObject json = jParser.makeHttpRequest(urlToGetAllPacks, "GET", params);
	        Log.d("All packs: ", json.toString());
	        
	        packsList = new ArrayList<PickerItem>();
	        try {
	        	if(json==null){
	        		isSuccess = false;
	        		return;
	        	}
	        	
	            // Checking for SUCCESS TAG
	            String status = json.getString(TAG_STATUS);

	            if (status.equals(TAG_SUCCESS)) {
	            	isSuccess = true;
	                packs = json.getJSONArray(TAG_PICKERS);
	                
	                for (int i = 0; i < packs.length(); i++) {
	                    JSONObject item = packs.getJSONObject(i);
	                    PickerItem picker = new PickerItem();
	                    picker.setFirstName(item.getString(TAG_FIRST_NAME));
	                    picker.setLastName(item.getString(TAG_LAST_NAME));
	                    picker.setMobileNumber(item.getString(TAG_MOBILE_NUMBER));
	                    picker.setTotal(item.getInt(TAG_TOTAL_PACK));
	                    packsList.add(picker);
	                }
	            }
	            else{
	            	isSuccess = false;
	            }
	        }catch (JSONException e) {
	            e.printStackTrace();
	        }
	    }

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}

		public void setPacksList(ArrayList<PickerItem> packsList) {
			this.packsList = packsList;
		}
		
		public String getUrlToGetAllPacks() {
			return urlToGetAllPacks;
		}

		public void setUrlToGetAllPacks(String urlToGetAllPacks) {
			this.urlToGetAllPacks = urlToGetAllPacks;
		}

		public ArrayList<PickerItem> getPacksList() {
			return packsList;
		}
	}