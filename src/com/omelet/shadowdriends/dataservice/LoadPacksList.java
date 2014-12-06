package com.omelet.shadowdriends.dataservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.omelet.shadowdriends.adapter.PacksListAdapter;
import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.location.GlobalLocation;
import com.omelet.shadowdriends.model.PackItem;
import com.omelet.shadowfriends.DetailsActivity;
import com.omelet.shadowfriends.util.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.ListView;

public class LoadPacksList extends AsyncTaskLoader<DataHandler> {

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
	    
		private static final String TAG_STATUS = "status";
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_REQUESTS = "requests";
		private static final String TAG_TITLE = "TITLE";
		private static final String TAG_DES = "DES";
		private static final String TAG_RID = "RID";
		private static final String TAG_OWNER = "OWNER";
		private static final String TAG_DISTANCE = "DISTANCE";
		private static final String TAG_CURRENT_STATUS = "CURRENT_STATUS";

		public LoadPacksList(Context context, Activity activity, ListView listView) {
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
	        params.add(new BasicNameValuePair("mobile_number", mobileNumber));
	        params.add(new BasicNameValuePair("latitude", String.valueOf(sourceLat)));
	        params.add(new BasicNameValuePair("longitude", String.valueOf(sourceLon)));
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
	            String status = json.getString(TAG_STATUS);

	            if (status.equals(TAG_SUCCESS)) {
	            	isSuccess = true;
	                packs = json.getJSONArray(TAG_REQUESTS);
	                
	                for (int i = 0; i < packs.length(); i++) {
	                    JSONObject nearItem = packs.getJSONObject(i);

	                    // Storing each json item in variable
	                    String title = nearItem.getString(TAG_TITLE);
	                    String des = nearItem.getString(TAG_DES);
	                    String rid = nearItem.getString(TAG_RID);
	                    int owner = nearItem.getInt(TAG_OWNER);
	                    int distance = nearItem.getInt(TAG_DISTANCE);
	                    
	                    PackItem packItem = new PackItem();
	                    packItem.setPackTitle(title);
	                    packItem.setPackDescription(des);
	                    packItem.setRid(rid);
	                    packItem.setOwner(owner);
	                    packItem.setDistance(distance);
	                    packItem.setCurrentStatus(nearItem.getString(TAG_CURRENT_STATUS));
	                    
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