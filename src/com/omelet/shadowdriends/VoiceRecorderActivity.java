package com.omelet.shadowdriends;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class VoiceRecorderActivity extends Activity {

	private ImageButton recordStartStop;
	private MediaRecorder myAudioRecorder;
	private String outputFile;

	private static final String AUDIO_RECORDER_FOLDER = "RecordedEvidence";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private boolean start = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_voice);
		recordStartStop = (ImageButton) findViewById(R.id.recordStartStop);
		recordStartStop.setBackgroundResource(R.drawable.record_on);
		recordStartStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (start) {
						Toast.makeText(getApplicationContext(), "Recording started",
								Toast.LENGTH_LONG).show();
						myAudioRecorder = new MediaRecorder();
						outputFile = getFilename();
						myAudioRecorder
								.setAudioSource(MediaRecorder.AudioSource.MIC);
						myAudioRecorder
								.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
						myAudioRecorder
								.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
						myAudioRecorder.setOutputFile(outputFile);
						recordStartStop.setBackgroundResource(R.drawable.record_off);
						start = false;
						myAudioRecorder.prepare();
						myAudioRecorder.start();
					} else {
						recordStartStop
								.setBackgroundResource(R.drawable.record_on);
						myAudioRecorder.stop();
						myAudioRecorder.release();
						start = true;
						Toast.makeText(getApplicationContext(), "Recording completed. You can find your evidence in RecordedEvidence folder.",
								Toast.LENGTH_LONG).show();
						
					}

				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
}