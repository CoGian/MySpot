package com.example.myspot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Alarm_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ALARM","Works");

        // fetch extra from intent
        double final_cost = intent.getExtras().getDouble("final_cost");

        //create an intent to the ringtone service
        Intent service_intent = new Intent(context, NotificationService.class);

        // pass the extra
        service_intent.putExtra("final_cost",final_cost);

        //start the ringtone service
        NotificationService.enqueueWork(context,service_intent);
    }
}
