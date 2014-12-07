package com.omelet.shadowdriends;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
 
public class StartAtBootServiceReceiver extends BroadcastReceiver 
{
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
			editor = preferences.edit();
			
			editor.putString("shake", "off");
			
			editor.putString("proximity_booth", "hide");
			editor.putString("proximity_gas_station", "hide");
			editor.putString("proximity_police_station", "hide");
			editor.putString("proximity_hospital", "hide");
			editor.putString("proximity_restaurant", "hide");
			
			editor.putString("prRadius", "1000");
			editor.putString("prRadiusIdx", "8");
			
			editor.commit();
			
			Log.d("on boot","called");
		}
	}
}