package com.omelet.shadowdriends.track;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.omelet.shadowfriends.util.Network;

public class TrackMeService extends Service {
	private Network netCheck;

	@Override
	public void onCreate() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		Intent myIntent = new Intent(getApplicationContext(),
				TrackMeService.class);
		PendingIntent pendingIntent = PendingIntent.getService(
				getApplicationContext(), 0, myIntent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.SECOND, 10);
			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
					pendingIntent);

		netCheck = new Network(this);
		boolean networkStatus = netCheck.isNetworkConnected();
		if (networkStatus == true) {
			new AddLocationActivity().execute("");
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}