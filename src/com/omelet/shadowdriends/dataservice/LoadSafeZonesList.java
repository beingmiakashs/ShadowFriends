package com.omelet.shadowdriends.dataservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.omelet.shadowdriends.DetailsActivity;
import com.omelet.shadowdriends.adapter.PacksListAdapter;
import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.location.GlobalLocation;
import com.omelet.shadowdriends.model.PackItem;
import com.omelet.shadowfriends.util.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.ListView;

public class LoadSafeZonesList extends AsyncTaskLoader<DataHandler> {

		DataHandler mHaDataHandler;
		
		Context mContext;
		private String mobileNumber;
		private String urlToGetAllPacks;
		private Activity mActivity;
		private ListView mListView;
		
		private JSONParser jParser;
		private JSONArray packs = null;
		private ArrayList<PackItem> packsList;
		private boolean isSuccess = false;

		private GPSTracker gps;
		private double sourceLat;
		private double sourceLon;
	    
		public static final String TAG_STATUS = "status";
		public static final String TAG_SUCCESS = "success";
		public static final String TAG_REQUESTS = "product";
		public static final String TAG_NAME = "NAME";
		public static final String TAG_ADDRESS = "ADDRESS";
		public static final String TAG_PHONE = "PHONE";
		public static final String TAG_EMAIL = "EMAIL";
		public static final String TAG_DISTANCE = "DISTANCE";
		public static final String TAG_LONGITUDE = "LONGITUDE";
		public static final String TAG_LATITUDE = "LATITUDE";

		public LoadSafeZonesList(Context context, Activity activity, ListView listView) {
			super(context);
			mContext = context;
			mActivity = activity;
			mListView = listView;
			mHaDataHandler = new DataHandler();
		}

		@Override
		public DataHandler loadInBackground() {
			
			loadPacksList();
			mHaDataHandler.setData(packsList);
			return mHaDataHandler;
		}

		@Override
		public void deliverResult(DataHandler data) {
			super.deliverResult(data);
			mListView.setAdapter(new PacksListAdapter(mActivity, data
					.getData()));
		}
		
		public void loadPacksList() {
			gps = new GPSTracker(mActivity);
			for (;;) {
				gps.getLocation();
				if (gps.canGetLocation()) {

					double latitudeFromTracker = gps.getLatitude();
					double longitudeFromTracker = gps.getLongitude();

					if (latitudeFromTracker < 1) {
						sourceLat = GlobalLocation.latitude;
						sourceLon = GlobalLocation.longitude;
					} else {
						GlobalLocation.latitude = latitudeFromTracker;
						GlobalLocation.longitude = longitudeFromTracker;

						sourceLat = GlobalLocation.latitude;
						sourceLon = GlobalLocation.longitude;
					}
					break;
				}
			}
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        // getting JSON string from URL
	        params.add(new BasicNameValuePair("type", "14"));
	        params.add(new BasicNameValuePair("Latitude", String.valueOf(sourceLat)));
	        params.add(new BasicNameValuePair("Longitude", String.valueOf(sourceLon)));
	        params.add(new BasicNameValuePair("Radious", "1000"));
	        jParser = new JSONParser(mContext);
	        JSONObject json = jParser.makeHttpRequest(urlToGetAllPacks, "GET", params);
	        Log.d("All packs: ", json.toString());
	        
	        packsList = new ArrayList<PackItem>();
	        try {
	        	if(json==null){
	        		isSuccess = false;
	        		return;
	        	}
	        	
	            // Checking for SUCCESS TAG
	            int status = json.getInt(TAG_SUCCESS);

	            if (status==1) {
	            	isSuccess = true;
	                packs = json.getJSONArray(TAG_REQUESTS);
	                
	                for (int i = 0; i < packs.length(); i++) {
	                    JSONObject nearItem = packs.getJSONObject(i);
	                    
	                    PackItem packItem = new PackItem();
	                    packItem.setName(nearItem.getString(TAG_NAME));
	                    packItem.setAddress(nearItem.getString(TAG_ADDRESS));
	                    packItem.setPhone(nearItem.getString(TAG_PHONE));
	                    packItem.setEmail(nearItem.getString(TAG_EMAIL));
	                    packItem.setLat(Double.valueOf(nearItem.getString(TAG_LATITUDE)));
	                    packItem.setLon(Double.valueOf(nearItem.getString(TAG_LONGITUDE)));
	                    packItem.setDistance(nearItem.getInt(TAG_DISTANCE));
	                    
	                    packsList.add(packItem);
	                }
	            }
	            else{
	            	isSuccess = false;
	            }
	        }catch (JSONException e) {
	            e.printStackTrace();
	        }
	    }
		
		public String getMobileNumber() {
			return mobileNumber;
		}

		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}

		public void setPacksList(ArrayList<PackItem> packsList) {
			this.packsList = packsList;
		}
		
		public String getUrlToGetAllPacks() {
			return urlToGetAllPacks;
		}

		public void setUrlToGetAllPacks(String urlToGetAllPacks) {
			this.urlToGetAllPacks = urlToGetAllPacks;
		}

		public ArrayList<PackItem> getPacksList() {
			return packsList;
		}
	}