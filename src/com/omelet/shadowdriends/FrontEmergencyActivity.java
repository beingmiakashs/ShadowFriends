package com.omelet.shadowdriends;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.location.GlobalLocation;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;

public class FrontEmergencyActivity extends Activity {
	
	private SharedPreferences preferences;
	private SmsManager smsManager;
	public String address;
	public GPSTracker gps;
	
	BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sendEmergencyHelp();
	}
	
	public void sendEmergencyHelp(){
		preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Network netCheck = new Network(this);
		boolean networkStatus = netCheck.isNetworkConnected();
		address = "";
		if(networkStatus){
			refreshLocation();
		}
		String message = "I need Emergency help. My approximate location is, Latitude : "+GlobalLocation.latitude
		+" and Longitude : "+GlobalLocation.longitude;
		if(address.length()>1){
			message += " . Address : "+address;
		}
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean sentSMSFlag = false;
		for(int i=0;i<4;i++){
			String status = preferences.getString(GlobalConstant.KEY_EMERGENCY_CONTACT+i, GlobalConstant.KEY_STATUS_NOT_SET);
			if(status.equals(GlobalConstant.KEY_STATUS_SET)){
				String number = preferences.getString(GlobalConstant.KEY_EMERGENCY_CONTACT_NUMBER+i, GlobalConstant.KEY_STATUS_NOT_SET);
				if(!number.equals(GlobalConstant.KEY_STATUS_NOT_SET)){
					sendSMS(number, message);
					sentSMSFlag = true;
				}
			}
		}
		if(!sentSMSFlag){
			Toast.makeText(getBaseContext(), "No emergency contact is found", Toast.LENGTH_LONG).show();
		}
		finish();
	}
	
	public void sendSMS(String number, String message){
		Toast.makeText(getBaseContext(), "Emergency message is sending", Toast.LENGTH_LONG).show();
		smsManager = SmsManager.getDefault();
		PendingIntent piSent=PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        PendingIntent piDelivered=PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
		smsManager.sendTextMessage(number, null, message, piSent, piDelivered);
	}
	
	public void refreshLocation(){
		gps = new GPSTracker(getBaseContext());
		if (gps.canGetLocation()) {

			double latitudeFromTracker = gps.getLatitude();
			double longitudeFromTracker = gps.getLongitude();

			if (latitudeFromTracker < 1) {} 
			else {
				GlobalLocation.latitude = latitudeFromTracker;
				GlobalLocation.longitude = longitudeFromTracker;
			}
		}
		address = "Address is not specified";

		try {
			address = getAddressForLocation(getApplicationContext());
		} catch (IOException e) {
			address = "";
		}
	}
	
	public String getAddressForLocation(Context context) throws IOException {

		double latitude = GlobalLocation.latitude;
		double longitude = GlobalLocation.longitude;
		int maxResults = 1;

		Geocoder gc = new Geocoder(context, Locale.getDefault());
		List<Address> addresses = gc.getFromLocation(latitude, longitude,maxResults);

		if (addresses.size() == 1) {
			String address = addresses.get(0).getFeatureName() + "\n"+ addresses.get(0).getLocality();
			return address;
		} else {
			return null;
		}
	}
	
	
	public void onResume() {
        super.onResume();
        smsSentReceiver=new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub
                switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS has been sent", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic Failure", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No Service", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio Off", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
                }
                
            }
        };
        smsDeliveredReceiver=new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub
                switch(getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS Delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        };
        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(smsDeliveredReceiver, new IntentFilter("SMS_DELIVERED"));
    }
    
	public void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

	
}
