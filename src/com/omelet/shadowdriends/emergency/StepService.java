package com.omelet.shadowdriends.emergency;

import java.util.ArrayList;

import com.omelet.shadowdriends.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.PowerManager.WakeLock;

public class StepService extends Service{
    private static boolean running = false;
    
    private static SensorManager sensorManager = null;
    private static StepDetector stepDetector = null;
    
    private static PowerManager powerManager = null;
    private static WakeLock wakeLock = null;
    private static NotificationManager notificatioManager = null;
    private static Notification notification = null;
    private static ArrayList<IStepServiceCallback> mCallbacks = new ArrayList<IStepServiceCallback>();

    @Override
    public void onCreate() {
        super.onCreate();

        notificatioManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        initNotification();

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepService");
        if (!wakeLock.isHeld()) wakeLock.acquire();

        stepDetector = new StepDetector();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener( stepDetector, 
                                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
                                        SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStart(Intent intent, int startId) {        
        super.onStart(intent, startId);
                
        running=true;
    }
    
    @Override
    public void onDestroy() {        
        super.onDestroy();
        
        notificatioManager.cancel(R.string.app_name);
        if (wakeLock.isHeld()) wakeLock.release();
        sensorManager.unregisterListener(stepDetector);
        
        running=false;
    }

    private void initNotification() {
        notification = new Notification(R.drawable.ic_launcher, "Emergency Shake is started.", System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        
        
        CharSequence text = getText(R.string.app_name);
        notification = new Notification(R.drawable.ic_launcher, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, EmergencyShakeSetting.class), 0);
        notification.setLatestEventInfo(this, text,"Emergency Service on Shake is turned on", contentIntent);

        notificatioManager.notify(R.string.app_name, notification);
    }

    private final IStepService.Stub mBinder = new IStepService.Stub(){
        @Override
        public boolean isRunning() throws RemoteException {
            return running;
        }

                @Override
                public void setSensitivity(int sens) throws RemoteException {
                    StepDetector.setSensitivity(sens);
                }  
                
        @Override
        public void registerCallback(IStepServiceCallback cb) throws RemoteException {
            if (cb != null) mCallbacks.add(cb);
        }

        @Override
        public void unregisterCallback(IStepServiceCallback cb) throws RemoteException {
            if (cb != null) mCallbacks.remove(cb);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
