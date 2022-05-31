package com.mind.blower.safety;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

public class ScreenReceiver extends BroadcastReceiver {

   int count = 0;


    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            count++;
                if(count == 4) {
                    count = 0;
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = new long[]{0, 400, 200, 400};
                    v.vibrate(pattern, -1);


                    try {
                        Everything everything = new Everything(context,new MainActivity());
                        everything.start();
                    }
                    catch (Exception e){
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }





//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Everything everything1 = new Everything(context,new MainActivity());
//                        everything1.sendNotification();
//
//
//                        }
//                    },10000);
            }
        }
    }




}
