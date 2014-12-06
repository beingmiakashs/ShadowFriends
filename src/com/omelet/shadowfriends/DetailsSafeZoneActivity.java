package com.omelet.shadowfriends;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import br.com.dina.ui.widget.UITableView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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
import com.omelet.shadowdriends.dataservice.LoadSafeZonesList;
import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.location.GlobalLocation;
import com.omelet.shadowdriends.model.RequestDetails;
import com.omelet.shadowfriends.util.GMapV2GetRouteDirection;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;

public class DetailsSafeZoneActivity extends FragmentActivity implements
		OnMarkerClickListener{

	private GoogleMap googleMap;
	private GPSTracker gps;
	public double sourceLat;
	public double sourceLon;
	public String address;
	public LoadMapBack loadMapBack;
	public Double queryRadious = 1000.0;
	public int placeType;
	public PolylineOptions rectLine = null;
	public GMapV2GetRouteDirection v2GetRouteDirection;
	public Document document;
	public Polyline polyLine;

	public int itemId;
	public String itemName;
	public String itemDescription;
	public String itemLatitude;
	public String itemLongitude;
	public String itemDistance;

	private String actionName;

	public ArrayList<Marker> markerList = new ArrayList();
	private Network netCheck;
	public boolean networkStatus;
	private String itemTitle;
	private String itemDes;
	private RequestDetails requestDetails;
	private TextView title;
	private TextView des;
	private ListView list;
	private UITableView numberList;
	private TelephonyManager telephonyManager;
	private PhoneStateListener listener;
	private TextView currentStatus;
	private String actionType;
	private Button callButton;
	private int owner;
	private SharedPreferences preferences;
	private boolean isFirstCall = false;
	private String phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		netCheck = new Network(this);
		networkStatus = netCheck.isNetworkConnected();
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		if (networkStatus == false) {
			setContentView(R.layout.no_internet);
			ImageButton img = (ImageButton) findViewById(R.id.internet_setting);
			img.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					GlobalConstant.mContext.startActivity(new Intent(
							WifiManager.ACTION_PICK_WIFI_NETWORK));
				}
			});
		} else {
			setContentView(R.layout.details_safezone_activity);

			int status = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getBaseContext());
			if (status != ConnectionResult.SUCCESS) {
				int requestCode = 10;
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
						this, requestCode);
				dialog.show();
			} else {
				title = (TextView) findViewById(R.id.title);
				currentStatus = (TextView) findViewById(R.id.currentStatus);
				des = (TextView) findViewById(R.id.des);
				callButton = (Button) findViewById(R.id.Call);
				
				phoneNumber = getIntent().getStringExtra(LoadSafeZonesList.TAG_PHONE);
				callButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						makeCall(phoneNumber);
					}
				});

				SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map);

				googleMap = fm.getMap();

				googleMap.setMyLocationEnabled(true);
				googleMap.getUiSettings().setZoomGesturesEnabled(true);
				googleMap.getUiSettings().setCompassEnabled(true);
				googleMap.getUiSettings().setMyLocationButtonEnabled(true);
				googleMap.getUiSettings().setRotateGesturesEnabled(true);

				googleMap.setOnMarkerClickListener(this);

				googleMap
						.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
							@Override
							public void onInfoWindowClick(Marker marker) {
							}
						});

				v2GetRouteDirection = new GMapV2GetRouteDirection();
				polyLine = null;

				itemTitle = getIntent().getStringExtra(LoadSafeZonesList.TAG_NAME);
				itemDes = getIntent().getStringExtra(LoadSafeZonesList.TAG_ADDRESS);

			}
		}
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		marker.showInfoWindow();
		Log.d("pos", marker.getPosition().toString());

		GetRouteTask routeTask = new GetRouteTask(new LatLng(
				GlobalLocation.latitude, GlobalLocation.longitude),
				marker.getPosition());
		routeTask.execute();

		return true;
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
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			gps = new GPSTracker(DetailsSafeZoneActivity.this);
		}

		protected String doInBackground(String... args) {

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
			address = "not specified";

			try {
				address = getAddressForLocation(getApplicationContext());
			} catch (IOException e) {
				address = "not specified";
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
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
					CircleOptions circleOptions = new CircleOptions()
							.center(point) // set center
							.radius(queryRadious) // set radius in meters
							.fillColor(0x1A0000FF) // default
							.strokeColor(Color.BLUE).strokeWidth(5);

					Circle myCircle = googleMap.addCircle(circleOptions);

					// add item marker for which this activity is called
					LatLng senderPoint = new LatLng(
							requestDetails.getSenderLat(),
							requestDetails.getSenderLon());
					MarkerOptions senderMarker = new MarkerOptions()
							.position(senderPoint)
							.title("Sender:" + requestDetails.getTitle())
							.snippet(requestDetails.getCurrentStatus());
					senderMarker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
					googleMap.addMarker(senderMarker);

					LatLng receiverPoint = new LatLng(
							requestDetails.getReceiverLat(),
							requestDetails.getReceiverLon());
					MarkerOptions receiverMarker = new MarkerOptions()
							.position(receiverPoint)
							.title("Receiver:" + requestDetails.getTitle())
							.snippet(requestDetails.getCurrentStatus());
					receiverMarker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					googleMap.addMarker(receiverMarker);

					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(senderPoint).zoom(12).build();
					googleMap.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
				}
			});

		}

	}

	private class GetRouteTask extends AsyncTask<String, Void, String> {

		String response = "";

		LatLng fromPosition;
		LatLng toPosition;

		GetRouteTask(LatLng _fromPosition, LatLng _toPosition) {
			fromPosition = _fromPosition;
			toPosition = _toPosition;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			Log.d("pos", fromPosition.toString() + " " + toPosition.toString());
			document = v2GetRouteDirection.getDocument(fromPosition,
					toPosition, GMapV2GetRouteDirection.MODE_DRIVING);
			response = "Success";
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (polyLine != null) {
						polyLine.remove();
					}
					if (response.equalsIgnoreCase("Success")) {
						ArrayList<LatLng> directionPoint = v2GetRouteDirection
								.getDirection(document);
						rectLine = new PolylineOptions().width(10).color(
								Color.RED);

						for (int i = 0; i < directionPoint.size(); i++) {
							rectLine.add(directionPoint.get(i));
						}
						polyLine = googleMap.addPolyline(rectLine);
					}
				}
			});
		}
	}

	public void makeCall(String number) {
		String phoneCallUri = "tel:";
		phoneCallUri += number;

		// Get the telephony manager
		telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		listener = new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				String stateString = "N/A";
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					stateString = "Idle";
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					stateString = "Off Hook";
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					stateString = "Ringing";
					break;
				}
			}
		};
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
		phoneCallIntent.setData(Uri.parse(phoneCallUri));
		this.startActivity(phoneCallIntent);

	}
}
