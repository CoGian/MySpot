package com.example.myspot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm_Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ALARM","Works");

        // fetch extra from intent
        Boolean alarmOn = intent.getExtras().getBoolean("alarmOn");

        //create an intent to the ringtone service
        Intent service_intent = new Intent(context,RingtonePlayingService.class);

        // pass the extra
        service_intent.putExtra("alarmOn",alarmOn);

        //start the ringtone service
        context.startService(service_intent);
    }
}
