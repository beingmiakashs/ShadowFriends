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

import org.json.JSONObject;
import org.w3c.dom.Document;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;

public class CreateWalkWithMeAcrivity extends FragmentActivity implements
		OnMapLongClickListener {

	private GoogleMap googleMap;
	private GPSTracker gps;
	public double sourceLat;
	public double sourceLon;
	private double pickupLat;
	private double pickupLon;
	private double destinationLat;
	private double destinationLon;
	private int actionSeq = 1;
	private Marker senderMarker = null;
	private Marker receiverMarker = null;
	public String address;
	public LoadMapBack loadMapBack;
	public Double queryRadious;
	public int placeType;
	public PolylineOptions rectLine = null;
	public Document document;
	public Polyline polyLine;

	public ArrayList<Marker> markerList = new ArrayList();
	private EditText searchText;
	private Network netCheck;

	public static int voiceCheck = 1639;
	
	AutoCompleteTextView atvPlaces;
	
	DownloadTask placesDownloadTask;
	DownloadTask placeDetailsDownloadTask;
	ParserTask placesParserTask;
	ParserTask placeDetailsParserTask;
	
	final int PLACES=0;
	final int PLACES_DETAILS=1;
	private Button nextAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		netCheck = new Network(this);
		boolean networkStatus = netCheck.isNetworkConnected();
		
		GlobalConstant.showMessage(CreateWalkWithMeAcrivity.this,"Press long on a place in the map which is your pickup location");

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
			setContentView(R.layout.create_pack);
			
			nextAction = (Button) findViewById(R.id.nextAction);
			
			atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
			atvPlaces.setThreshold(1);		
			
			// Adding textchange listener
			atvPlaces.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {	
					placesDownloadTask = new DownloadTask(PLACES);
					String url = getAutoCompleteUrl(s.toString());
					placesDownloadTask.execute(url);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
			
			atvPlaces.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
	                            long id) {

	                    ListView lv = (ListView) arg0;
	                    SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

	                    HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);                    
	                    placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);
	                    String url = getPlaceDetailsUrl(hm.get("reference"));    				
	    				placeDetailsDownloadTask.execute(url);
	            }
			});

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
				
				nextAction.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(actionSeq==1){
							if(senderMarker==null){
								GlobalConstant.showMessage(CreateWalkWithMeAcrivity.this, "Press long on a place in the map which is your pickup location");
							}
							else{
								GlobalConstant.showMessage(CreateWalkWithMeAcrivity.this, "Press long on a place in the map which is your destination location");
								actionSeq = 2;
								nextAction.setText("Confirm your destination location.");
							}
						}
						if(actionSeq==2){
							if(receiverMarker==null){
								GlobalConstant.showMessage(CreateWalkWithMeAcrivity.this, "Press long on a place in the map which is your destination location");
							}
							else{
								Intent i = new Intent(CreateWalkWithMeAcrivity.this, SubmitWalkWithMe.class);
								i.putExtra(GlobalConstant.TAG_SENDER_LAT, pickupLat);
								i.putExtra(GlobalConstant.TAG_SENDER_LON, pickupLon);
								i.putExtra(GlobalConstant.TAG_RECEIVER_LAT, destinationLat);
								i.putExtra(GlobalConstant.TAG_RECEIVER_LON, destinationLat);
								Log.d("intent data", pickupLat+" "+pickupLon+" "+destinationLat+" "+destinationLon);
								startActivity(i);
								finish();
							}
						}
					}
				});
				
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

				googleMap.setOnMapLongClickListener(this);

				queryRadious = 100000.0;

				loadMapBack = new LoadMapBack();
				loadMapBack.execute();
			}
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {

		/*Intent in = new Intent(getApplicationContext(), InsertData.class);

		in.putExtra(GlobalConstant.KEY_ACTION_TYPE,
				GlobalConstant.KEY_ACTION_SP_INSERT);

		in.putExtra("lat", point.latitude);
		in.putExtra("lon", point.longitude);
		startActivityForResult(in, 1);*/
		createMarker(point);
	}
	
	private void createMarker(LatLng point) {
		String title="";
		if(actionSeq==1){
			title = "Pickup location of the user"; 
		}
		else if(actionSeq==2){
			title = "Destination of this user journey"; 
		}
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(point);
		markerOptions.title(title);
		if(actionSeq==1)
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		else if(actionSeq==2)
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		Marker tempMarker = googleMap.addMarker(markerOptions);
		if(actionSeq==1){
			if(senderMarker!=null)senderMarker.remove();
			senderMarker = tempMarker;
			pickupLat = point.latitude;
			pickupLon = point.longitude;
			Log.d("pickup location", pickupLat+" "+pickupLon);
		}
		else if(actionSeq==2){
			if(receiverMarker!=null)receiverMarker.remove();
			receiverMarker = tempMarker;
			destinationLat = point.latitude;
			destinationLon = point.longitude;
			Log.d("receiver location", destinationLat+" "+destinationLon);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK, null);
			finish();
		}
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

			gps = new GPSTracker(CreateWalkWithMeAcrivity.this);
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
	
	
	
	
	private String getAutoCompleteUrl(String place){
		
		// Obtain browser key from https://code.google.com/apis/console
		String key = "key=AIzaSyCbXGqeu71gGAxJrLiZjrHVOWQC8fQLo3c";
					
		// place to be be searched
		String input = "input="+place;
					
		// place type to be searched
		String types = "types=geocode";
					
		// Sensor enabled
		String sensor = "sensor=false";			
					
		// Building the parameters to the web service
		String parameters = input+"&"+types+"&"+sensor+"&"+key;
					
		// Output format
		String output = "json";
					
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;
		
		return url;
	}
	
	
	private String getPlaceDetailsUrl(String ref){
		
		// Obtain browser key from https://code.google.com/apis/console
		String key = "key=AIzaSyCbXGqeu71gGAxJrLiZjrHVOWQC8fQLo3c";
					
		// reference of place
		String reference = "reference="+ref;					
					
		// Sensor enabled
		String sensor = "sensor=false";			
					
		// Building the parameters to the web service
		String parameters = reference+"&"+sensor+"&"+key;
					
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;
		
		return url;
	}
	
	/** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }
    
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{
		
		private int downloadType=0;
		
		// Constructor
		public DownloadTask(int type){
			this.downloadType = type;			
		}

		@Override
		protected String doInBackground(String... url) {
			
			// For storing data from web service
			String data = "";
			
			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
                Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);		
			
			switch(downloadType){
			case PLACES:
				// Creating ParserTask for parsing Google Places
				placesParserTask = new ParserTask(PLACES);
				
				// Start parsing google places json data
				// This causes to execute doInBackground() of ParserTask class
				placesParserTask.execute(result);
				
				break;
				
			case PLACES_DETAILS : 
				// Creating ParserTask for parsing Google Places
				placeDetailsParserTask = new ParserTask(PLACES_DETAILS);
				
				// Starting Parsing the JSON string
				// This causes to execute doInBackground() of ParserTask class
				placeDetailsParserTask.execute(result);								
			}			
		}		
	}
	
	
	/** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

    	int parserType = 0;
    	
    	public ParserTask(int type){
    		this.parserType = type;
    	}
    	
		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {
			
			JSONObject jObject;			
			List<HashMap<String, String>> list = null;           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	
            	switch(parserType){
            	case PLACES :
            		PlaceJSONParser placeJsonParser = new PlaceJSONParser();
		            // Getting the parsed data as a List construct
		            list = placeJsonParser.parse(jObject);
		            break;
            	case PLACES_DETAILS :      	            	
	            	PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
	            	// Getting the parsed data as a List construct
	               	list = placeDetailsJsonParser.parse(jObject);
            	}

            }catch(Exception e){
            	Log.d("Exception",e.toString());
            }
            return list;
		}
		
		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {
			
				switch(parserType){
				case PLACES :
					String[] from = new String[] { "description"};
					int[] to = new int[] { android.R.id.text1 };
						
					// Creating a SimpleAdapter for the AutoCompleteTextView
					SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);				
						
					// Setting the adapter
					atvPlaces.setAdapter(adapter);						
					break;
				case PLACES_DETAILS :						
					HashMap<String, String> hm = result.get(0);
					
					// Getting latitude from the parsed data 
					double latitude = Double.parseDouble(hm.get("lat"));
					
					// Getting longitude from the parsed data
					double longitude = Double.parseDouble(hm.get("lng"));										
					
					
					LatLng point = new LatLng(latitude, longitude);
					
					CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(point);
					CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(5);
					
					// Showing the user input location in the Google Map					
					googleMap.moveCamera(cameraPosition);
					googleMap.animateCamera(cameraZoom);
					
					MarkerOptions options = new MarkerOptions();
					options.position(point);
					options.title(hm.get("name"));
					options.snippet("Address:"+hm.get("vicinity")+".Latitude:"+latitude+",Longitude:"+longitude);
					
					// Adding the marker in the Google Map
					googleMap.addMarker(options);
					
					break;						
				}			
		}			
    }    

}
