package com.example.myspot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class NotificationService extends JobIntentService {
    public static final int JOB_ID = 1;

    int startId ;
    boolean isRunning  ;
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("ALARM", "Received start id " + startId + ": " + intent);

        // fetch extra
        Boolean alarmOn = intent.getExtras().getBoolean("alarmOn");

        Log.e("ALARM",""+ alarmOn);

        // converts the extra to startId
        assert alarmOn != null;
        if (alarmOn) startId = 1;
        else startId = 0;

        // if there is no music playing and user switch alarm on , music should start playing
        if (!this.isRunning && startId == 1){
            Log.e("ALARM","There is no music an we want");
            // notification

            // set up an intent that goes to maps activity
            Intent maps_intent = new Intent(this.getApplicationContext(),MapsActivity.class);
            maps_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // set up pending intent
            PendingIntent pending_maps_Intent = PendingIntent.getActivity(this,0,maps_intent
                    ,0) ;

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            // initialize builder for users with oreo and up
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                CharSequence name = "MySpot";
                String description = "My spot channel";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("3000", name, importance);
                channel.setDescription(description);
                channel.setSound(alarmSound,att);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                // delete previous channels
                if (notificationManager != null) {
                    List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

                    for (int i = 0; channelList != null && i < channelList.size(); i++) {
                        notificationManager.deleteNotificationChannel(channelList.get(i).getId());
                    }
                }
                notificationManager.createNotificationChannel(channel);
            }

            // set up notification's parameters
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"3000")
                    .setSmallIcon(R.drawable.ic_icon_notification)
                    .setContentTitle("MySpot")
                    .setContentText("You must unpark")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pending_maps_Intent)
                    .setAutoCancel(true)
                    .setColorized(true)
                    .setSound(alarmSound)
                    .setOngoing(true);

            // take notification manager
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // build notification
            Notification notification = builder.build();

            // set on loop till user tap it
            notification.flags = Notification.FLAG_INSISTENT;

            // notify
            notificationManager.notify(1, notification);

            this.isRunning = true;
        }
        // if music playing and user press alarm off  , music should stop
        else if(this.isRunning && startId == 0 ){
            Log.e("ALARM","There is music an we don't want");


            this.isRunning = false;
        }
        // if random button pressed
        //  if there is no music playing and user press alarm off , do nothing
        else if(!this.isRunning && startId == 0){
            Log.e("ALARM","There is no music and we press alarm off");
            this.isRunning = false;
        }
        // if music playing and user press alarm on , do nothing
        else if (this.isRunning && startId == 1){
            Log.e("ALARM","There is  music an we press alarm on ");
            this.isRunning = true;
        }
        else{
            Log.e("ALARM","Somehow you reach that ");
        }


    }
}
