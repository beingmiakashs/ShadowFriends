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

public class CreateAccount extends AsyncTask<String, String, String> {

	private Context mContext;
	private ProgressDialog pDialog;
	private OnTaskCompleted mListener;

	private String firstName;
	private String lastName;
	private String pid;
	private String mobileNumber;
	private String password;
	private String urlToGetDetails;

	private JSONParser jParser;
	private JSONArray packs = null;
	private boolean isSuccess = false;

	private static final String TAG_STATUS = "status";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	
	
	public CreateAccount(Context mContext, OnTaskCompleted mListener,
			String firstName, String lastName, String pid, String mobileNumber,
			String password, String urlToGetDetails) {
		super();
		this.mContext = mContext;
		this.mListener = mListener;
		this.firstName = firstName;
		this.lastName = lastName;
		this.pid = pid;
		this.mobileNumber = mobileNumber;
		this.password = password;
		this.urlToGetDetails = urlToGetDetails;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Creating your account. Please wait...");
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
		params.add(new BasicNameValuePair("name", firstName+" "+lastName));
		params.add(new BasicNameValuePair("pid", pid));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("userid", mobileNumber));
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
		mListener.onTaskCompleted("");
	}

	public String getUrlToGetDetails() {
		return urlToGetDetails;
	}

	public void setUrlToGetDetails(String urlToGetDetails) {
		this.urlToGetDetails = urlToGetDetails;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
}
