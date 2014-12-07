package com.omelet.shadowdriends;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

public class VoiceRecorderActivity extends Activity {

	private static final String AUDIO_RECORDER_FOLDER = "RecordedEvidence";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	
	private ImageButton recordStartStop;
	private MediaRecorder myAudioRecorder;
	private String outputFile;

	private boolean start = true;
	private int  previousVolume;
	private AudioManager audio;
    private boolean volumeChange = true;
    private SharedPreferences settings;
    private Editor editor;
    private CheckBox selectRecordOnVolumeChange;
    private SharedPreferences sharedPrefs;
    private ContentObserver mVolumeObserver;
    private boolean recordStartedByVolumeKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_voice);
		selectRecordOnVolumeChange = (CheckBox) findViewById(R.id.selectRecordOnVolumeChange);
		
	    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		editor = sharedPrefs.edit();
		boolean isChecked = sharedPrefs.getBoolean("checkedRecordOnVolumeKeyPress", false);
	
		if (isChecked){
			selectRecordOnVolumeChange.setChecked(true);
		} else {
			selectRecordOnVolumeChange.setChecked(false);
		}
		
		audio = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);	       
		previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		mVolumeObserver = new ContentObserver(new Handler()) {
	        @Override
	        public void onChange(boolean selfChange) {
	            super.onChange(selfChange);
	            int updatedVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
//	            Toast.makeText(getApplicationContext(), "updated" + updatedVolume + " prev volume " + previousVolume, Toast.LENGTH_LONG).show();	
	            if (updatedVolume - previousVolume != 0) {
	            	recordVoice();
	            }
	            previousVolume = updatedVolume;
	        }
	    };
	    
		selectRecordOnVolumeChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		       @Override
		       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		    	   
		    	   if (isChecked) {
			    	   editor.putBoolean("checkedRecordOnVolumeKeyPress", true);
			    	   editor.commit();
			    	   getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mVolumeObserver);
			    	   Toast.makeText(getApplicationContext(), "Emergency Recording feature enabled", Toast.LENGTH_LONG).show();
		    	   } else {
		    		   editor.putBoolean("checkedRecordOnVolumeKeyPress", false);
			    	   editor.commit(); 
			    	   getContentResolver().unregisterContentObserver(mVolumeObserver);			   		
		    	   }	    	   
		       }
		   }
		);     
		recordStartStop = (ImageButton) findViewById(R.id.recordStartStop);
		recordStartStop.setBackgroundResource(R.drawable.record_on);
		recordStartStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					recordVoice();		 //todo :// keep one copy of record voice		
			}

			private void recordVoice() {
				if (start) {
					Toast.makeText(getApplicationContext(), "Recording started",
							Toast.LENGTH_LONG).show();
					myAudioRecorder = new MediaRecorder();
					outputFile = getFilename();
					myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
					myAudioRecorder.setOutputFile(outputFile);
					recordStartStop.setBackgroundResource(R.drawable.record_off);
					start = false;
					try {
						myAudioRecorder.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					myAudioRecorder.start();
				} else {
					recordStartStop.setBackgroundResource(R.drawable.record_on);
					myAudioRecorder.stop();
					myAudioRecorder.release();
					start = true;
					Toast.makeText(getApplicationContext(), "Recording completed. Recorded File Successfully saved in " + AUDIO_RECORDER_FOLDER,
							Toast.LENGTH_LONG).show();
					
				}
				
			}
		});	
			
	}

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_MP4);
	}
	private void recordVoice() {
		if (start) {
			Toast.makeText(getApplicationContext(), "Recording started",
					Toast.LENGTH_LONG).show();
			myAudioRecorder = new MediaRecorder();
			outputFile = getFilename();
			myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
			myAudioRecorder.setOutputFile(outputFile);
			recordStartStop.setBackgroundResource(R.drawable.record_off);
			start = false;
			try {
				myAudioRecorder.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myAudioRecorder.start();
		} else {
			recordStartStop.setBackgroundResource(R.drawable.record_on);
			myAudioRecorder.stop();
			myAudioRecorder.release();
			start = true;
			Toast.makeText(getApplicationContext(), "Recording completed. Recorded File Successfully saved in " + AUDIO_RECORDER_FOLDER,
					Toast.LENGTH_LONG).show();
			
		}
	}
}