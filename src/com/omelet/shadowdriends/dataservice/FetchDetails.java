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

public class FetchDetails extends AsyncTask<String, String, String> {

	private Context mContext;
	private ProgressDialog pDialog;
	private OnTaskCompleted mListener;

	private String rid;
	private RequestDetails requestDetails;
	private String urlToGetDetails;

	private JSONParser jParser;
	private JSONArray packs = null;
	private boolean isSuccess = false;

	private static final String TAG_STATUS = "status";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_DETAILS = "details";
	public static final String TAG_TITLE = "TITLE";
	public static final String TAG_DES = "DES";
	public static final String TAG_RID = "RID";
	private static final String TAG_SENDER_PID = "SENDER_PID";
	private static final String TAG_RECEIVER_PID = "RECEIVER_PID";
	private static final String TAG_CARRIER_PID = "CARRIER_PID";
	private static final String TAG_CURRENT_STATUS = "CURRENT_STATUS";
	private static final String TAG_SENDER_LON = "SENDER_LON";
	private static final String TAG_SENDER_LAT = "SENDER_LAT";
	private static final String TAG_RECEIVER_LON = "RECEIVER_LON";
	private static final String TAG_RECEIVER_LAT = "RECEIVER_LAT";
	private static final String TAG_SENDER_NAME = "SENDER_NAME";
	private static final String TAG_RECEIVER_NAME = "RECEIVER_NAME";
	private static final String TAG_CARRIER_NAME = "CARRIER_NAME";

	public FetchDetails(Context context, OnTaskCompleted listener,
			RequestDetails requestDetails) {
		mContext = context;
		mListener = listener;
		this.requestDetails = requestDetails;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Loading pack details. Please wait...");
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
				JSONObject item = json.getJSONObject(TAG_DETAILS);
				requestDetails.setTitle(item.getString(TAG_TITLE));
				requestDetails.setDes(item.getString(TAG_DES));
				requestDetails.setRid(item.getString(TAG_RID));
				requestDetails.setSenderContactNumber(item.getString(TAG_SENDER_PID));
				requestDetails.setReceiverContactNumber(item.getString(TAG_RECEIVER_PID));
				requestDetails.setCarrierContactNumber(item.getString(TAG_CARRIER_PID));
				requestDetails.setSenderLat(item.getDouble(TAG_SENDER_LAT));
				requestDetails.setSenderLon(item.getDouble(TAG_SENDER_LON));
				requestDetails.setReceiverLat(item.getDouble(TAG_RECEIVER_LAT));
				requestDetails.setReceiverLon(item.getDouble(TAG_RECEIVER_LON));
				requestDetails.setCurrentStatus(item.getString(TAG_CURRENT_STATUS));
				requestDetails.setSenderName(item.getString(TAG_SENDER_NAME));
				requestDetails.setReceiverName(item.getString(TAG_RECEIVER_NAME));
				requestDetails.setCarrierName(item.getString(TAG_CARRIER_NAME));
			} else {
				isSuccess = false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void onPostExecute(String file_url) {
		pDialog.dismiss();
		mListener.onTaskCompleted("");
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getUrlToGetDetails() {
		return urlToGetDetails;
	}

	public void setUrlToGetDetails(String urlToGetDetails) {
		this.urlToGetDetails = urlToGetDetails;
	}

	public RequestDetails getRequestDetails() {
		return requestDetails;
	}

	public void setRequestDetails(RequestDetails requestDetails) {
		this.requestDetails = requestDetails;
	}
}
