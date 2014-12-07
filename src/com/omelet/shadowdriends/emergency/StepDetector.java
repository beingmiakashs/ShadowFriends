package com.omelet.shadowdriends.emergency;

import java.util.ArrayList;

import com.omelet.shadowfriends.util.GlobalConstant;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ParserError", "ParserError" })
@SuppressWarnings("deprecation")
public class StepDetector implements SensorEventListener {
	
	private static int mLimit = 100;

	private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();

	/* This section is used to detect shake event */
	/****************** START ********************/

	/** Minimum movement force to consider. */
	private static final int MIN_FORCE = 15;

	/**
	 * Minimum times in a shake gesture that the direction of movement needs to
	 * change.
	 */
	private static int MIN_DIRECTION_CHANGE = 3;

	/** Maximum pause between movements. */
	private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;

	/** Maximum allowed time for shake gesture. */
	private static final int MAX_TOTAL_DURATION_OF_SHAKE = 500;

	/** Time when the gesture started. */
	public static long mFirstDirectionChangeTime = 0;

	/** Time when the last movement started. */
	public static long mLastDirectionChangeTime;

	/** How many movements are considered so far. */
	public static int mDirectionChangeCount = 0;

	/** The last x position. */
	private float lastX = 0;

	/** The last y position. */
	private float lastY = 0;

	/** The last z position. */
	
	private float lastZ = 0;
	/****************** END ********************/

	public StepDetector() {
	}

	public static void setSensitivity(int sensitivity) {
        mLimit = sensitivity;
        MIN_DIRECTION_CHANGE = sensitivity;
        
        //Log.e("sensitivity inside", ""+MIN_DIRECTION_CHANGE);
    }

	public void addStepListener(StepListener sl) {
		mStepListeners.add(sl);
	}

	@SuppressLint({ "ParserError", "ParserError" })
	@Override
	public void onSensorChanged(SensorEvent se) {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GlobalConstant.mContext);
		MIN_DIRECTION_CHANGE = Integer.parseInt(preferences.getString("shake_sensitivity", "4"));

		// get sensor data
		float x = se.values[SensorManager.DATA_X];
		float y = se.values[SensorManager.DATA_Y];
		float z = se.values[SensorManager.DATA_Z];

		// calculate movement
		float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

		if (totalMovement > MIN_FORCE) {

			// get time
			long now = System.currentTimeMillis();

			// store first movement time
			if (mFirstDirectionChangeTime == 0) {
				mFirstDirectionChangeTime = now;
				mLastDirectionChangeTime = now;
			}

			// check if the last movement was not long ago
			long lastChangeWasAgo = now - mLastDirectionChangeTime;
			if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {

				// store movement data
				mLastDirectionChangeTime = now;
				mDirectionChangeCount++;

				// store last sensor data
				lastX = x;
				lastY = y;
				lastZ = z;

				// check how many movements are so far
				if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {

					// check total duration
					long totalDuration = now - mFirstDirectionChangeTime;
					if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
						
						resetShakeParameters();
						
						String status = preferences.getString("shake_type", "0");
						String status1 = preferences.getString("shake", "0");
						
						if(status1.equals("on"))
						{
							Log.e("shake police call", "one time call from service");
							
							/****************** START Activity ****************/
							Intent i = new Intent();
							i.setClassName("com.omelet.shadowdriends", "com.omelet.shadowdriends.FrontEmergencyActivity");
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.putExtra("type", status);
							EmergencyShakeSetting.getContext().startActivity(i);
							
							
							/**************** END ***********************/
						}
						
					}
				}

			} else {
				resetShakeParameters();
			}
		}
	}
	
	private void resetShakeParameters() {
		mFirstDirectionChangeTime = 0;
		mDirectionChangeCount = 0;
		mLastDirectionChangeTime = 0;
		lastX = 0;
		lastY = 0;
		lastZ = 0;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}