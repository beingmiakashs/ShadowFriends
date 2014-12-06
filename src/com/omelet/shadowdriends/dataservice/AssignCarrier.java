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
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.JSONParser;
import com.omelet.shadowfriends.util.OnTaskCompleted;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class AssignCarrier extends AsyncTask<String, String, String> {

	private Context mContext;
	private ProgressDialog pDialog;
	private OnTaskCompleted mListener;

	private String rid;
	private String mobileNumber;
	private String carrierMobileNumber;
	private String urlToGetDetails;

	private JSONParser jParser;
	private JSONArray packs = null;
	private boolean isSuccess = false;

	private static final String TAG_STATUS = "status";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	public AssignCarrier(Context mContext, OnTaskCompleted mListener,
			String rid, String carrierMobileNumber, String mobileNumber,
			String urlToGetDetails) {
		this.mContext = mContext;
		this.mListener = mListener;
		this.rid = rid;
		this.carrierMobileNumber = carrierMobileNumber;
		this.mobileNumber = mobileNumber;
		this.urlToGetDetails = urlToGetDetails;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Assigning carrier. Please wait...");
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
		params.add(new BasicNameValuePair("rid", rid));
		params.add(new BasicNameValuePair("mobile_number", mobileNumber));
		params.add(new BasicNameValuePair("cid", carrierMobileNumber));
		jParser = new JSONParser(mContext);
		JSONObject json = jParser.makeHttpRequest(urlToGetDetails, "GET",
				params);
		try {
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
		}
	}

	protected void onPostExecute(String file_url) {
		pDialog.dismiss();
		mListener.onTaskCompleted(GlobalConstant.ACTION_ASSIGN_CARRIER);
	}
}
