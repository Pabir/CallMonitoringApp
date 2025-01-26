package com.yourpackage.callmonitoringapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import androidx.core.app.NotificationCompat;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CallMonitoringService extends Service {

    private static final String CHANNEL_ID = "call_monitoring_channel";
    private static final int NOTIFICATION_ID = 1;
    private TelephonyManager telephonyManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the notification channel for devices running Android 8.0 and above
        //createNotificationChannel();

        // Create and show the notification immediately when service starts
        startForeground(NOTIFICATION_ID, getNotification());

        // Set up phone state listener to monitor calls
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d("CallMonitoringService", "Incoming call from: " + incomingNumber);
                        // Log the incoming call to the database
                        CallLogDatabaseHelper dbHelper = new CallLogDatabaseHelper(CallMonitoringService.this);
                        dbHelper.addCall(incomingNumber, "Incoming", 0);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d("CallMonitoringService", "Call Answered");
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d("CallMonitoringService", "Call Ended");
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "call_monitoring_channel";
            String channelName = "Call Monitoring Service";
            String channelDescription = "Monitors incoming and outgoing calls";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification getNotification() {
    String channelId = "your_channel_id";

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(channelId, "Call Monitoring", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);
    }

    return new NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call Monitoring Active")
            .setContentText("Monitoring incoming and outgoing calls.")
            .setSmallIcon(R.drawable.ic_call)
            .build();
}


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Service will restart if killed by the system
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (telephonyManager != null) {
            telephonyManager.listen(null, PhoneStateListener.LISTEN_NONE); // Stop listening to phone state
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
