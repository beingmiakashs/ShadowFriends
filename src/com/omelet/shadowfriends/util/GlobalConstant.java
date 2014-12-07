package com.omelet.shadowfriends.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public final class GlobalConstant {
	public static Context mContext;

	public static final String KEY_STATUS_ON = "on";
	public static final String KEY_STATUS_OFF = "off";

	public static final String KEY_RID = "rid";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_DISTANCE = "distance";
	
	public static final String USER_ID = "USER_ID";
	public static final String LOGIN_ACCESSTOKEN = "ACCESSTOKEN";
	public static final String LOGIN_STATUS = "LOGIN_STATUS";
	public static final String LOGIN_TYPE = "LOGIN_TYPE";
	public static final String LOGIN_LOCAL = "LOGIN_LOCAL";
	public static final String LOGIN_FB = "LOGIN_FB";
	public static final String LOGIN_STATUS_SIGNIN = "SIGNIN";
	public static final String LOGIN_STATUS_SIGNOUT = "SIGNOUT";
	
	public static final String PACK_OWNER = "PACK_OWNER";
	public static final String ACTION_TYPE = "ACTION_TYPE";
	public static final String ACTION_REQUESTING = "Requesting";
	public static final String ACTION_ACCEPTING = "Accepting";
	public static final String ACTION_CARRYNG = "Carrying";
	public static final String ACTION_CONFIRMANTION = "Confirmation";
	public static final String ACTION_DELIVERED = "Delivered";
	public static final String ACTION_CANCEL = "Cancel";
	public static final String ACTION_DECLINE = "Decline";
	
	public static final String TAG_SENDER_LON = "SENDER_LON";
	public static final String TAG_SENDER_LAT = "SENDER_LAT";
	public static final String TAG_RECEIVER_LON = "RECEIVER_LON";
	public static final String TAG_RECEIVER_LAT = "RECEIVER_LAT";
	
	public static final String ACTION_UPDATE = "ACTION_UPDATE";
	public static final String ACTION_ASSIGN_CARRIER = "ACTION_ASSIGN_CARRIER";
	
	public static final String KEY_STATUS_SET = "set";
	public static final String KEY_STATUS_NOT_SET = "not_set";
	
	public static final String KEY_EMERGENCY_CONTACT = "EMERGENCY_CONTACT_";
	public static final String KEY_EMERGENCY_CONTACT_NAME = "EMERGENCY_CONTACT_NAME_";
	public static final String KEY_EMERGENCY_CONTACT_NUMBER = "EMERGENCY_CONTACT_NUMBER_";
	
	public static final String KEY_GEAR_APP_START_EMERGENCY = "GEAR_APP_START_EMERGENCY";
	public static final String KEY_GEAR_APP_BUTTON_PRESS_START = "GEAR_APP_BUTTON_PRESS_START";

	public static final String KEY_GEAR_APP_BUTTON_PRESS_EVENT = "GearAppButtonClickEvent";
	public static final String KEY_GEAR_APP_START_EVENT = "GearAppStartEvent";
	
	public static final String KEY_GEAR_APP_STOP_EVENT = "gear_app_stop_event";
	
	public static final String KEY_EMERGENCY_WHISTLE = "emergency_whistle";
	public static final String KEY_EMERGENCY_LIGHT = "emergency_light";
	
	public static void showMessage(final Context context, final String message){
		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}
