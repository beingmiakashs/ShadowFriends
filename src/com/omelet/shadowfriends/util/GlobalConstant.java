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
	
	
	
	

	//----------------------------------------------------------------------
	    public static final String TAG_CONTACTS = "requests";


	    public static final String TAG_ID = "userid";
	    //public static final String TAG_User_ID_value = "anik";


	    public static final String TAG_REQ_FID = "fid";

	    public static final String TAG_REQ_TITLE = "title";
	    public static final String TAG_REQ_DATE = "email";
	    public static final String TAG_REQ_STATUS = "des";
	    public static final String TAG_REQ_SUB_STATUS = "label";

	    public static final String TAG_REQ_TOTAL_AGREE = "total_agree";
	    public static final String TAG_REQ_TOTAL_DISAGREE = "total_disagree";

	    public static final String TAG_REQ_USER_LATITUDE = "lat";
	    public static final String TAG_REQ_USER_LONGITUDE = "lon";

	    public static final String TAG_FID = "FID";
	    public static final String TAG_TITLE = "TITLE";
	    public static final String TAG_DATE = "email";
	    public static final String TAG_STATUS = "DES";
	    public static final String TAG_SUB_STATUS = "LABEL";

	    public static final String TAG_TOTAL_AGREE = "TOTAL_AGREE";
	    public static final String TAG_TOTAL_DISAGREE = "TOTAL_DISAGREE";

	    public static final String TAG_USER_LATITUDE = "LAT";
	    public static final String TAG_USER_LONGITUDE = "LON";

	    public static String insertCommentUrl=	"http://www.omleteit.com/apps/shadow/comment.php";


	    public static String insertVoteUrl= "http://www.omleteit.com/apps/shadow/vote.php?userid=anik&fid=7&action=disagree";
	    public static String insertPostUrl = "http://www.omleteit.com/apps/shadow/creatPost.php";
	    public static String getPostUrl = "http://www.omleteit.com/apps/shadow/feedList.php?userid=anik&lat=23.752157&lon=90.390873";
	    public static String getButtonStatus ="http://www.omleteit.com/apps/shadow/GetSinglePostVote.php?userid=anik&fid=7";
	    //-------------------------------------------------------

	    
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
