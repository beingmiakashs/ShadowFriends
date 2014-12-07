package com.omelet.shadowdriends;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

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
import com.omelet.shadowdriends.dataservice.AssignCarrier;
import com.omelet.shadowdriends.dataservice.UpdateStatus;
import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.location.GlobalLocation;
import com.omelet.shadowdriends.model.RequestDetails;
import com.omelet.shadowfriends.util.GMapV2GetRouteDirection;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;
import com.omelet.shadowfriends.util.OnTaskCompleted;

public class DetailsActivity extends FragmentActivity implements
		OnMarkerClickListener, OnTaskCompleted {

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
	private String itemRid;
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
	private Button actionButtonOne;
	private Button actionButtonTwo;
	private int owner;
	private SharedPreferences preferences;
	private boolean isFirstCall = false;

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
			setContentView(R.layout.details_activity);

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
				actionButtonOne = (Button) findViewById(R.id.actionOne);
				actionButtonTwo = (Button) findViewById(R.id.actionTwo);
				actionType = getIntent().getStringExtra(
						GlobalConstant.ACTION_TYPE);
				owner = getIntent().getIntExtra(GlobalConstant.PACK_OWNER, 4);
				setButtonAction();

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

				/*itemRid = getIntent().getStringExtra(FetchDetails.TAG_RID);
				itemTitle = getIntent().getStringExtra(FetchDetails.TAG_TITLE);
				itemDes = getIntent().getStringExtra(FetchDetails.TAG_DES);*/
			}
		}
	}

	private void setButtonAction() {
		Log.d("action button",actionType+" "+owner+" "+preferences.getString(GlobalConstant.USER_ID, "**"));
		if(actionType.equals(GlobalConstant.ACTION_REQUESTING)  && owner==1){
		}
		else if (actionType.equals(GlobalConstant.ACTION_ACCEPTING)  && owner==3) {
			actionButtonOne.setText("Accept Request");
			actionButtonTwo.setText("Decline");
		} else if (actionType.equals(GlobalConstant.ACTION_CARRYNG)  && owner==3) {
			actionButtonOne.setText("Handover Pack");
			actionButtonTwo.setVisibility(View.GONE);
		} else if (actionType.equals(GlobalConstant.ACTION_CONFIRMANTION) && owner==2) {
			actionButtonOne.setText("Confirm Delivery");
			actionButtonTwo.setVisibility(View.GONE);
		}
		else{
			actionButtonOne.setVisibility(View.GONE);
			actionButtonTwo.setVisibility(View.GONE);
		}
		actionButtonOne.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String mobileNumber = preferences.getString(GlobalConstant.USER_ID, "");
				Log.d("button click",actionType);
				if (actionType.equals(GlobalConstant.ACTION_REQUESTING)) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetailsActivity.this);
					alertDialog.setTitle("Carrier Mobile Number");

					alertDialog.setMessage("Enter carrier registered mobile number");
					final EditText input = new EditText(DetailsActivity.this);
					input.setRawInputType(Configuration.KEYBOARD_QWERTY);
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.MATCH_PARENT);
					input.setLayoutParams(lp);
					alertDialog.setView(input);
					alertDialog.setPositiveButton("Assign carrier",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String carrierMobileNumber = input.getText().toString();
									String urlToGetDetails = "http://omleteit.com/apps/pickpack/assignCarrier.php";
									if (carrierMobileNumber.compareTo("")!=0) {
										AssignCarrier assignCarrier = new AssignCarrier(DetailsActivity.this, DetailsActivity.this,
												itemRid, carrierMobileNumber, preferences.getString(GlobalConstant.USER_ID, ""),
												urlToGetDetails);
										assignCarrier.execute();
									}
									else{
										GlobalConstant.showMessage(DetailsActivity.this, "Enter a valid mobile number");
									}
								}
							});
					alertDialog.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									dialog.cancel();
								}
							});
					alertDialog.show();
				} else if (actionType.equals(GlobalConstant.ACTION_ACCEPTING)) {
					new UpdateStatus(DetailsActivity.this,
							DetailsActivity.this, itemRid,
							GlobalConstant.ACTION_CARRYNG, mobileNumber,
							"http://omleteit.com/apps/pickpack/updateRequest.php")
							.execute();
				} else if (actionType.equals(GlobalConstant.ACTION_CARRYNG)) {
					new UpdateStatus(DetailsActivity.this,
							DetailsActivity.this, itemRid,
							GlobalConstant.ACTION_CONFIRMANTION, mobileNumber,
							"http://omleteit.com/apps/pickpack/updateRequest.php")
							.execute();
				} else if (actionType
						.equals(GlobalConstant.ACTION_CONFIRMANTION)) {
					new UpdateStatus(DetailsActivity.this,
							DetailsActivity.this, itemRid,
							GlobalConstant.ACTION_DELIVERED, mobileNumber,
							"http://omleteit.com/apps/pickpack/updateRequest.php")
							.execute();
				}
			}
		});
		actionButtonTwo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String mobileNumber = preferences.getString(
						GlobalConstant.USER_ID, "");
				if (actionType.equals(GlobalConstant.ACTION_REQUESTING)) {
					new UpdateStatus(DetailsActivity.this,
							DetailsActivity.this, itemRid,
							GlobalConstant.ACTION_DECLINE, mobileNumber,
							"http://omleteit.com/apps/pickpack/updateRequest.php")
							.execute();
				} else if (actionType.equals(GlobalConstant.ACTION_ACCEPTING)) {
					new UpdateStatus(DetailsActivity.this,
							DetailsActivity.this, itemRid,
							GlobalConstant.ACTION_DECLINE, mobileNumber,
							"http://omleteit.com/apps/pickpack/updateRequest.php")
							.execute();
				}
			}
		});
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
			gps = new GPSTracker(DetailsActivity.this);
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

	@Override
	public void onTaskCompleted(String status) {
		if(status.equals(GlobalConstant.ACTION_ASSIGN_CARRIER) || status.equals(GlobalConstant.ACTION_UPDATE)){
			Log.d("get update", "hello");
			setResult(RESULT_OK, null);
			finish();
		}
		
		title.setText(requestDetails.getTitle());
		currentStatus.setText(requestDetails.getCurrentStatus());
		des.setText(requestDetails.getDes());
		numberList = (UITableView) findViewById(R.id.numberList);
		createList();
		numberList.commit();

		loadMapBack = new LoadMapBack();
		loadMapBack.execute();
	}

	private void createList() {
		CustomClickListener listener = new CustomClickListener();
		numberList.setClickListener(listener);
		populateNumberList();
	}

	private void populateNumberList() {

		if (requestDetails.getSenderName().length() > 2) {
			numberList.addBasicItem(R.drawable.user_image, "Sender: "
					+ requestDetails.getSenderName(),
					requestDetails.getSenderContactNumber());
		} else {
			numberList.addBasicItem(R.drawable.user_image, "Sender",
					"Not Assigned");
		}

		if (requestDetails.getReceiverName().length() > 2) {
			numberList.addBasicItem(R.drawable.user_image, "Receiver: "
					+ requestDetails.getReceiverName(),
					requestDetails.getReceiverContactNumber());
		} else {
			numberList.addBasicItem(R.drawable.user_image, "Receiver",
					"Not Assigned");
		}

		if (requestDetails.getCarrierName().length() > 2) {
			numberList.addBasicItem(R.drawable.user_image, "Carrier: "
					+ requestDetails.getCarrierName(),
					requestDetails.getCarrierContactNumber());
		} else {
			numberList.addBasicItem(R.drawable.user_image, "Carrier",
					"Not Assigned");
		}
	}

	private class CustomClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			if (index == 0) {
				if (requestDetails.getSenderContactNumber().length() > 2) {
					makeCall(requestDetails.getSenderContactNumber());
				}
			}
			if (index == 1) {
				if (requestDetails.getReceiverContactNumber().length() > 2) {
					makeCall(requestDetails.getReceiverContactNumber());
				}
			}
			if (index == 2) {
				if (requestDetails.getCarrierContactNumber().length() > 2) {
					makeCall(requestDetails.getCarrierContactNumber());
				}
			}
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

	@Override
	public void showMessage(String message) {
		GlobalConstant.showMessage(DetailsActivity.this, message);
	}
}
