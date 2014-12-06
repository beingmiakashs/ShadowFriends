package com.omelet.shadowdriends.createpack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.location.GlobalLocation;
import com.omelet.shadowdriends.model.PackItem;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.JSONParser;
import com.omelet.shadowfriends.util.Network;
import com.omelet.shadowfriends.util.OnTaskCompleted;

public class ShowWalkWithMeAcrivity extends FragmentActivity {

	private GoogleMap googleMap;
	private GPSTracker gps;
	public double sourceLat;
	public double sourceLon;
	public String address;
	public LoadMapBack loadMapBack;
	public Double queryRadious;
	public int placeType;
	public PolylineOptions rectLine = null;
	public Document document;
	public Polyline polyLine;

	public ArrayList<Marker> markerList = new ArrayList();
	private Network netCheck;

	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		netCheck = new Network(this);
		boolean networkStatus = netCheck.isNetworkConnected();
		
		GlobalConstant.showMessage(ShowWalkWithMeAcrivity.this,"Press long on a place in the map which is your pickup location");

		if (networkStatus == false) {
			setContentView(R.layout.no_internet);
			ImageButton img = (ImageButton) findViewById(R.id.internet_setting);
			img.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					GlobalConstant.mContext.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
				}
			});
		} else {
			setContentView(R.layout.show_walkwithme);
			
			// Getting Google Play availability status
			int status = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getBaseContext());

			// Showing status
			if (status != ConnectionResult.SUCCESS) { // Google Play Services
														// are not available
				int requestCode = 10;
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
						this, requestCode);
				dialog.show();

			} else { // Google Play Services are available
								
				SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map);

				// Getting GoogleMap object from the fragment
				googleMap = fm.getMap();

				// Enabling MyLocation Layer of Google Map
				googleMap.setMyLocationEnabled(true);
				googleMap.getUiSettings().setZoomGesturesEnabled(true);
				googleMap.getUiSettings().setCompassEnabled(true);
				googleMap.getUiSettings().setMyLocationButtonEnabled(true);
				googleMap.getUiSettings().setRotateGesturesEnabled(true);

				queryRadious = 10000.0;

				loadMapBack = new LoadMapBack();
				loadMapBack.execute();
			}
		}
	}	
	private void createMarker(LatLng point, String title, int type) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(point);
		markerOptions.title(title);
		if(type==1)
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		else if(type==2)
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		Marker tempMarker = googleMap.addMarker(markerOptions);		
	}

	private void drawMarker(LatLng point, String title, String description) {

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(point);
		markerOptions.snippet(description);
		markerOptions.title(title);

		Marker marker = googleMap.addMarker(markerOptions);
		markerList.add(marker);
	}

	public String getAddressForLocation(Context context) throws IOException {

		double latitude = sourceLat;
		double longitude = sourceLon;
		int maxResults = 1;

		Geocoder gc = new Geocoder(context, Locale.getDefault());
		List<Address> addresses = gc.getFromLocation(latitude, longitude,
				maxResults);

		if (addresses.size() == 1) {

			String address = addresses.get(0).getFeatureName() + "\n"
					+ addresses.get(0).getLocality();

			return address;
		} else {
			return null;
		}
	}

	public class LoadMapBack extends AsyncTask<String, String, String> {

		LoadMapBack() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			gps = new GPSTracker(ShowWalkWithMeAcrivity.this);
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {

			// check if GPS enabled
			for(;;)
			{
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
			address = "not specified";

			try {
				address = getAddressForLocation(getApplicationContext());
			} catch (IOException e) {
				address = "not specified";
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			
			GetWalkWithMeData walkWithMeData = new GetWalkWithMeData();
			walkWithMeData.execute();
			
			runOnUiThread(new Runnable() {
				public void run() {

					LatLng point = new LatLng(sourceLat, sourceLon);

					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(point);
					markerOptions.snippet(address);
					markerOptions.title("My Current Location");
					markerOptions.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.user_map));

					googleMap.addMarker(markerOptions);

					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(sourceLat, sourceLon)).zoom(12)
							.build();
					googleMap.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));

					CircleOptions circleOptions = new CircleOptions()
							.center(point) // set center
							.radius(queryRadious) // set radius in meters
							.fillColor(0x1A0000FF) // default
							.strokeColor(Color.BLUE).strokeWidth(5);

					Circle myCircle = googleMap.addCircle(circleOptions);

				}
			});
		}
	}
	
	
	
	private class GetWalkWithMeData extends AsyncTask<String, Void, String>{
		
		private ProgressDialog pDialog;

		private JSONParser jParser;
		private JSONArray packs = null;
		private boolean isSuccess = false;

		private static final String TAG_STATUS = "status";
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MESSAGE = "message";
		private static final String TAG_REQUESTS = "requests";
		
		private JSONObject json;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ShowWalkWithMeAcrivity.this);
			pDialog.setMessage("Getting walk with me request. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			loadDetails();
			return null;
		}

		private void loadDetails() {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			params.add(new BasicNameValuePair("userid", preferences.getString(GlobalConstant.USER_ID, "")));
			params.add(new BasicNameValuePair("access_token", preferences.getString(GlobalConstant.LOGIN_ACCESSTOKEN, "")));
			Log.d("access",preferences.getString(GlobalConstant.LOGIN_ACCESSTOKEN, ""));
			jParser = new JSONParser(ShowWalkWithMeAcrivity.this);
			json = jParser.makeHttpRequest("http://www.omleteit.com/apps/shadow/Facebook_list.php", "GET",
					params);
			Log.d("All walk with me: ", json.toString());
			
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			
			runOnUiThread(new Runnable() {
				public void run() {

					try {
						if (json == null) {
							isSuccess = false;
							return;
						}
						String status = json.getString(TAG_STATUS);

			            if (status.equals(TAG_SUCCESS)) {
			            	isSuccess = true;
			                packs = json.getJSONArray(TAG_REQUESTS);
			                
			                for (int i = 0; i < packs.length(); i++) {
			                    JSONObject nearItem = packs.getJSONObject(i);
			                    
			                    createMarker(new LatLng(nearItem.getDouble("StartLat"), nearItem.getDouble("StartLng")), nearItem.getString("NAME")+" : "+nearItem.getString("Title"), 1);
			                    createMarker(new LatLng(nearItem.getDouble("DesLat"), nearItem.getDouble("DesLng")), nearItem.getString("NAME")+" : "+nearItem.getString("Title"), 1);
			                }
			            }
			            else{
			            	isSuccess = false;
			            }
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
		}	
	}
	
}
