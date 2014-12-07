package com.omelet.shadowdriends.track;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.location.GlobalLocation;
import com.omelet.shadowfriends.util.GlobalConstant;

public class AddLocationActivity extends AsyncTask<String, String, String> {

	private GPSTracker gps;
	public double sourceLat;
	public double sourceLon;
	
	private SharedPreferences preferences;

	@Override
	protected String doInBackground(String... params) {
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
		}
		return LoadStream(getImei(), sourceLat + "", sourceLon + "");
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		gps = new GPSTracker(GlobalConstant.mContext);
		preferences = PreferenceManager.getDefaultSharedPreferences(GlobalConstant.mContext);
	}

	public String LoadStream(String imei, String lat, String lon) {
		InputStream is = null;
		String result = "";
		JSONArray jArray = null;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://www.omleteit.com/apps/shadow/PublishMe.php");
			List nameValuePairs = new ArrayList(2);
						
			nameValuePairs.add(new BasicNameValuePair("userid", preferences.getString(GlobalConstant.USER_ID, "anik")));
			nameValuePairs.add(new BasicNameValuePair("lat", lat));
			nameValuePairs.add(new BasicNameValuePair("lng", lon));
			httppost.setEntity((new UrlEncodedFormEntity(nameValuePairs)));

			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString() + " "
					+ imei + " " + lat + " " + lon);
		}

		return result;
	}

	private String getImei() {
		String deviceID = null;
		String serviceName = Context.TELEPHONY_SERVICE;
		TelephonyManager m_telephonyManager = (TelephonyManager) GlobalConstant.mContext
				.getSystemService(serviceName);
		int deviceType = m_telephonyManager.getPhoneType();
		switch (deviceType) {
		case (TelephonyManager.PHONE_TYPE_GSM):
			break;
		case (TelephonyManager.PHONE_TYPE_CDMA):
			break;
		case (TelephonyManager.PHONE_TYPE_NONE):
			break;
		default:
			break;
		}
		deviceID = m_telephonyManager.getDeviceId();
		return deviceID;
	}

}
