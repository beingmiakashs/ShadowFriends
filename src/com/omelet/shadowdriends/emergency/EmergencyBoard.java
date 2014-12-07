package com.omelet.shadowdriends.emergency;

import java.util.Calendar;

import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.createpack.ShowWalkWithMeAcrivity;
import com.omelet.shadowdriends.track.TrackMeService;
import com.omelet.shadowfriends.util.GlobalConstant;

import br.com.dina.ui.model.ViewItem;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EmergencyBoard extends Activity {

	UITableView emergencyList;
	UITableView settingsList;

	private Uri uriContact;
	int PICK_CONTACT = 1;
	private String contactID;
	
	private SharedPreferences preferences;
	private Editor editor;

	private static final String TAG = "Life Gear Debug";
	
	private CheckBox gearAppStartCheck;
	private CheckBox gearAppButtonPressCheck;
	
	private Intent myIntent;
	private PendingIntent pendingIntent;
	ProgressDialog dialouge;
	private AlarmManager alarmManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();

		emergencyList = (UITableView) findViewById(R.id.emergencyList);
		createList();
		emergencyList.commit();
		
		settingsList = (UITableView) findViewById(R.id.settingsList);
		createSettingList();
		settingsList.commit();
	}

	private void createList() {
		CustomClickListener listener = new CustomClickListener();
		emergencyList.setClickListener(listener);
		populateEmergencyList();
	}
	private void populateEmergencyList(){
		// First emergency person contact
		String firstPersonName = getContactName(0);
		String firstPersonNumber = getContactNumber(0);
		Log.d("emergency one", firstPersonName+" "+firstPersonNumber);
		if(!firstPersonName.equals(firstPersonNumber)){
			emergencyList.addBasicItem(R.drawable.user_image,firstPersonName, firstPersonNumber);
		}
		else{
			emergencyList.addBasicItem(R.drawable.user_image,firstPersonName);
		}
		// Second emergency person contact
		String secondPersonName = getContactName(1);
		String secondPersonNumber = getContactNumber(1);
		if(!secondPersonName.equals(secondPersonNumber)){
			emergencyList.addBasicItem(R.drawable.user_image,secondPersonName, secondPersonNumber);
		}
		else{
			emergencyList.addBasicItem(R.drawable.user_image,secondPersonName);
		}
		// Third emergency person contact
		String thirdPersonName = getContactName(2);
		String thirdPersonNumber = getContactNumber(2);
		if(!thirdPersonName.equals(thirdPersonNumber)){
			emergencyList.addBasicItem(R.drawable.user_image,thirdPersonName, thirdPersonNumber);
		}
		else{
			emergencyList.addBasicItem(R.drawable.user_image,thirdPersonName);
		}
		// Fourth emergency person contact
		String fourthPersonName = getContactName(3);
		String fourthPersonNumber = getContactNumber(3);
		if(!fourthPersonName.equals(fourthPersonNumber)){
			emergencyList.addBasicItem(R.drawable.user_image,fourthPersonName, fourthPersonNumber);
		}
		else{
			emergencyList.addBasicItem(R.drawable.user_image,fourthPersonName);
		}
	}

	private void createSettingList() {
		
		SettingClickListener listener = new SettingClickListener();
		settingsList.setClickListener(listener);
		
		String gearAppStartSettingStatus = preferences.getString(GlobalConstant.KEY_GEAR_APP_START_EMERGENCY, GlobalConstant.KEY_STATUS_NOT_SET);
		
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout gearAppStartSettingView = (RelativeLayout) mInflater.inflate(R.layout.setting_item_app_start, null);
		gearAppStartCheck = (CheckBox)gearAppStartSettingView.findViewById(R.id.appStartCheckBox);
		if(gearAppStartSettingStatus.equals(GlobalConstant.KEY_STATUS_SET)){
			gearAppStartCheck.setChecked(true);
		}
		else{
			gearAppStartCheck.setChecked(false);
		}
		gearAppStartCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(gearAppStartCheck.isChecked()){
					Toast.makeText(getBaseContext(), "Activated", Toast.LENGTH_LONG).show();
					editor.putString(GlobalConstant.KEY_GEAR_APP_START_EMERGENCY, GlobalConstant.KEY_STATUS_SET);
				
					myIntent = new Intent(EmergencyBoard.this, TrackMeService.class);
					pendingIntent = PendingIntent.getService(EmergencyBoard.this, 0,myIntent, 0);
					alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.add(Calendar.SECOND, 5);
					alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
					
				}else{
					Toast.makeText(getBaseContext(), "Deactivated", Toast.LENGTH_LONG).show();
					editor.putString(GlobalConstant.KEY_GEAR_APP_START_EMERGENCY, GlobalConstant.KEY_STATUS_NOT_SET);
				}
				editor.commit();
			}
		});
		ViewItem gearAppStartSettingItem = new ViewItem(gearAppStartSettingView);
		settingsList.addViewItem(gearAppStartSettingItem);
		
		settingsList.addBasicItem("Shake Settings to send emergency SMS from your mobile");
		
		settingsList.addBasicItem("Create an emergency shortcut to send emergency SMS from your mobile");
	}
	
	private class CustomClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			if (isEmergencyContactSet(index)) {
				PICK_CONTACT = index;
				Intent pickContactIntent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
				pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
				startActivityForResult(pickContactIntent, PICK_CONTACT);
			}
		}
	}
	
	private class SettingClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			if(index==1){
				Intent in = new Intent(getApplicationContext(), EmergencyShakeSetting.class);
				startActivity(in);
			}
			else if(index==2)
			{
				Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		        shortcutIntent.setClassName("com.omelet.shadowfriends.emergency", "com.omelet.shadowfriends.emergency.EmergencySMSFromShortcut");
		        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        
		        Intent addIntent = new Intent();
		        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "LifeGear");
		        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(EmergencyBoard.this, R.drawable.sos));
		        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		        EmergencyBoard.this.sendBroadcast(addIntent);
				
				Toast.makeText(getBaseContext(), "Emergency SMS on mobile shortcut press is activated", Toast.LENGTH_LONG).show();
		
			}
		}
	}

	private boolean isEmergencyContactSet(int index) {
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			if (uri != null) {
				Cursor c = null;
				try {
					c = getContentResolver()
							.query(uri,
									new String[] {
											ContactsContract.CommonDataKinds.Phone.NUMBER,
											ContactsContract.CommonDataKinds.Phone.TYPE,
											ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },
									null, null, null);

					if (c != null && c.moveToFirst()) {
						String number = c.getString(0);
						int type = c.getInt(1);
						String contactName = c.getString(2);
						setEmergencyContact(PICK_CONTACT, contactName, number);
						Log.d("phone number",PICK_CONTACT+" "+number+" "+contactName);
					}
				} finally {
					if (c != null) {
						c.close();
					}
				}

			}
		}
	}
	
	private String getContactName(int index){
		String phoneNumber = preferences.getString(GlobalConstant.KEY_EMERGENCY_CONTACT+index, GlobalConstant.KEY_STATUS_NOT_SET);
		if(phoneNumber != GlobalConstant.KEY_STATUS_NOT_SET){
			return preferences.getString(GlobalConstant.KEY_EMERGENCY_CONTACT_NAME+index, "Emergency person contact");
		}
		else{
			return "Emergency person contact";
		}
	}
	
	private String getContactNumber(int index){
		String phoneNumber = preferences.getString(GlobalConstant.KEY_EMERGENCY_CONTACT+index, GlobalConstant.KEY_STATUS_NOT_SET);
		if(phoneNumber != GlobalConstant.KEY_STATUS_NOT_SET){
			return preferences.getString(GlobalConstant.KEY_EMERGENCY_CONTACT_NUMBER+index, "Press to set emergency contact");
		}
		else{
			return "Press to set emergency contact";
		}
	}
	
	private void setEmergencyContact(int index, String name, String number){
		if(isUniqueContact(number)){
			editor.putString(GlobalConstant.KEY_EMERGENCY_CONTACT+index, GlobalConstant.KEY_STATUS_SET);
			editor.putString(GlobalConstant.KEY_EMERGENCY_CONTACT_NAME+index, name);
			editor.putString(GlobalConstant.KEY_EMERGENCY_CONTACT_NUMBER+index, number);
			editor.commit();
			
			emergencyList.clear();
			populateEmergencyList();
			emergencyList.commit();
		}
		else{
			String message;
			if(!name.equals(number)){
				message = name+" ("+number+")";
			}
			else{
				message = name;
			}
			Toast.makeText(getBaseContext(), message+" contact is already added in the emergency contact list." +
					"Please select another contact.", Toast.LENGTH_LONG).show();
		}
	}
	
	private boolean isUniqueContact(String phoneNumber){
		for(int i=0;i<4;i++){
			if(getContactNumber(i).equals(phoneNumber)){
				return false;
			}
		}
		return true;
	}

}
