package com.omelet.shadowdriends.dataservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.omelet.shadowdriends.model.PackItem;
import com.omelet.shadowdriends.model.RequestDetails;
import com.omelet.shadowfriends.util.JSONParser;
import com.omelet.shadowfriends.util.OnTaskCompleted;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetWalkWithMeService extends AsyncTask<String, String, String> {

	private Context mContext;
	private ProgressDialog pDialog;
	private OnTaskCompleted mListener;

	private String mobileNumber;
	private String receiverMobileNumber;
	private double senderLat;
	private double senderLon;
	private double receiverLat;
	private double receiverLon;
	private String title;
	private String des;
	private String senderAddress;
	private String receiverAddress;
	private String urlToGetDetails;

	private JSONParser jParser;
	private JSONArray packs = null;
	private boolean isSuccess = false;

	private static final String TAG_STATUS = "status";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	
	
	
	public GetWalkWithMeService(Context mContext, OnTaskCompleted mListener,
			String mobileNumber, String receiverMobileNumber, double senderLat,
			double senderLon, double receiverLat, double receiverLon,
			String title, String des, String senderAddress,
			String receiverAddress, String urlToGetDetails) {
		super();
		this.mContext = mContext;
		this.mListener = mListener;
		this.mobileNumber = mobileNumber;
		this.receiverMobileNumber = receiverMobileNumber;
		this.senderLat = senderLat;
		this.senderLon = senderLon;
		this.receiverLat = receiverLat;
		this.receiverLon = receiverLon;
		this.title = title;
		this.des = des;
		this.senderAddress = senderAddress;
		this.receiverAddress = receiverAddress;
		this.urlToGetDetails = urlToGetDetails;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Submitting a new walk with me request. Please wait...");
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
		params.add(new BasicNameValuePair("userid", mobileNumber));
		params.add(new BasicNameValuePair("access_token", title));
		jParser = new JSONParser(mContext);
		JSONObject json = jParser.makeHttpRequest(urlToGetDetails, "GET",
				params);
		Log.d("All walk with me: ", json.toString());
		/*try {
			if (json == null) {
				isSuccess = false;
				return;
			}
			String status = json.getString(TAG_STATUS);

			if (status.equals(TAG_SUCCESS)) {
				isSuccess = true;
				mListener.showMessage(json.getString(TAG_MESSAGE));
			} else {
				isSuccess = false;
				mListener.showMessage(json.getString(TAG_MESSAGE));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
	}

	protected void onPostExecute(String file_url) {
		pDialog.dismiss();
		mListener.onTaskCompleted("");
	}

	public boolean isSuccess() {
		return isSuccess;
	}
}
