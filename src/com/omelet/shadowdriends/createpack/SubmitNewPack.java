package com.omelet.shadowdriends.createpack;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.dataservice.SubmitPackService;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.OnTaskCompleted;

public class SubmitNewPack extends Activity implements OnTaskCompleted {
	
	public EditText title;
	public EditText des;
	
	private int Id;
	private String name;
	private String description;
	private double lon;
	private double lat;
	private String actionType;
	private EditText receiverMobileNumber;
	private EditText senderAddress;
	private EditText receiverAddress;
	private double senderLat;
	private double senderLon;
	private double receiverLat;
	private double receiverLon;
	private SharedPreferences preferences;
	private String mobileNumber;
	private String urlToGetDetails;
	private SubmitPackService submitPack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_data);
		
		Button btn_submit = (Button) findViewById(R.id.submit);
		Button btnDelete = (Button) findViewById(R.id.delete);
		btnDelete.setVisibility(View.INVISIBLE);
		btnDelete.setEnabled(false);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mobileNumber = preferences.getString(GlobalConstant.USER_ID, "");
		urlToGetDetails = "http://omleteit.com/apps/pickpack/creatRequest.php";
		
		title = (EditText) findViewById(R.id.title);
		des = (EditText) findViewById(R.id.description);
		receiverMobileNumber = (EditText) findViewById(R.id.receiverMobileNumber);
		senderAddress = (EditText) findViewById(R.id.senderAddress);
		receiverAddress = (EditText) findViewById(R.id.receiverAddress);
		receiverMobileNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
						
		senderLat = getIntent().getDoubleExtra(GlobalConstant.TAG_SENDER_LAT, 0.0);
		senderLon = getIntent().getDoubleExtra(GlobalConstant.TAG_SENDER_LON, 0.0);
		receiverLat = getIntent().getDoubleExtra(GlobalConstant.TAG_RECEIVER_LAT, 0.0);
		receiverLon = getIntent().getDoubleExtra(GlobalConstant.TAG_RECEIVER_LON, 0.0);
		
		Log.d("senderLat", senderLat+" "+senderLon+" "+receiverLat+" "+receiverLon);
		
		/*actionType = getIntent().getStringExtra(GlobalConstant.KEY_ACTION_TYPE);
		
		if(actionType.equals(GlobalConstant.KEY_ACTION_SP_EDIT))
		{
			Id = Integer.parseInt(getIntent().getStringExtra(GlobalConstant.KEY_ID));
			name = getIntent().getStringExtra(GlobalConstant.KEY_NAME);
			description = getIntent().getStringExtra(GlobalConstant.KEY_DESCRIPTION);
			
			txt_name.setText(name);
			txt_des.setText(description);
		}*/
		
		btn_submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
				/*if(actionType.equals(GlobalConstant.KEY_ACTION_SP_INSERT))
				{
				}
				else if(actionType.equals(GlobalConstant.KEY_ACTION_SP_EDIT))
				{
				}*/
				submitPack = new SubmitPackService(SubmitNewPack.this, SubmitNewPack.this,
						mobileNumber, receiverMobileNumber.getText().toString(), senderLat, senderLon,
						receiverLat, receiverLon, title.getText().toString(), des.getText().toString(),
						senderAddress.getText().toString(), receiverAddress.getText().toString(),
						urlToGetDetails);
				submitPack.execute();
			}
		});
	}

	@Override
	public void onTaskCompleted(String status) {
		if(submitPack.isSuccess())finish();
	}

	@Override
	public void showMessage(String message) {
		GlobalConstant.showMessage(SubmitNewPack.this, message);
	}

}
