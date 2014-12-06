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
import com.omelet.shadowdriends.dataservice.GetWalkWithMeService;
import com.omelet.shadowdriends.dataservice.SubmitPackService;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.OnTaskCompleted;

public class SubmitWalkWithMe extends Activity implements OnTaskCompleted {
	
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
	
	private GetWalkWithMeService getWalkWithMe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_data);
		
		Button btn_submit = (Button) findViewById(R.id.submit);
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mobileNumber = preferences.getString(GlobalConstant.USER_ID, "");
		urlToGetDetails = "http://www.omleteit.com/apps/shadow/CreateJourney.php";
		
		title = (EditText) findViewById(R.id.title);
		des = (EditText) findViewById(R.id.description);
						
		senderLat = getIntent().getDoubleExtra(GlobalConstant.TAG_SENDER_LAT, 0.0);
		senderLon = getIntent().getDoubleExtra(GlobalConstant.TAG_SENDER_LON, 0.0);
		receiverLat = getIntent().getDoubleExtra(GlobalConstant.TAG_RECEIVER_LAT, 0.0);
		receiverLon = getIntent().getDoubleExtra(GlobalConstant.TAG_RECEIVER_LON, 0.0);
				
		btn_submit.setOnClickListener(new View.OnClickListener() {

			

			@Override
			public void onClick(View view) {
				
				submitPack = new SubmitPackService(SubmitWalkWithMe.this, SubmitWalkWithMe.this,
						mobileNumber, "", senderLat, senderLon,
						receiverLat, receiverLon, title.getText().toString(), des.getText().toString(),
						"", "",
						urlToGetDetails);
				submitPack.execute();
				
				
			}
		});
	}

	@Override
	public void onTaskCompleted(String status) {
		//if(submitPack.isSuccess())finish();
	}

	@Override
	public void showMessage(String message) {
		GlobalConstant.showMessage(SubmitWalkWithMe.this, message);
	}

}
