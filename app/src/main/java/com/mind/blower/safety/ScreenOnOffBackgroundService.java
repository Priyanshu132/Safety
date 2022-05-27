package com.mind.blower.safety;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class ScreenOnOffBackgroundService extends Service {

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    private ScreenReceiver screenReceiver = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_START_FOREGROUND_SERVICE)){


            NotificationChannel channel = new NotificationChannel("Channel","Channel",NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "Channel")
                    .setOngoing(true)
                    .setContentTitle("SOS Activate")
                    .setContentText("Service is Running");


            startForeground(1001, notification.build());

            IntentFilter intentFilter = new IntentFilter();

            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
            intentFilter.setPriority(100);

             screenReceiver = new ScreenReceiver();
            registerReceiver(screenReceiver, intentFilter);


        }
        else if (intent.getAction().equals( ACTION_STOP_FOREGROUND_SERVICE)){
            unregisterReceiver(screenReceiver);
            Log.e("Service","Stopping...");
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


}
