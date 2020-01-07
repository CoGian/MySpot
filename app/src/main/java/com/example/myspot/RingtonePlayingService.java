package com.example.myspot;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class RingtonePlayingService extends Service {

    MediaPlayer media_song;
    int startId ;
    boolean isRunning  ;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
            // create an instance of the media player
            media_song = MediaPlayer.create(RingtonePlayingService.this,R.raw.no_good_for_me);
            // start ringtone
            media_song.start();

            this.isRunning = true;
        }
        // if music playing and user press alarm off  , music should stop
        else if(this.isRunning && startId == 0 ){
            Log.e("ALARM","There is music an we don't want");

            //stop ringtone
            media_song.stop();
            media_song.reset();

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


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Log.e("ALARM","DESTROYED");
        super.onDestroy();
        this.isRunning = false;
    }


}
