package com.example.myspot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    TimePicker alarm_timePicker ;
    Context context;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        this.context = this ;
        // initialize alarm manager
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        // initialize timepicker
        alarm_timePicker = findViewById(R.id.alarmTimePicker);

        // create an instance of a calendar
        final Calendar calendar = Calendar.getInstance();

        // create an intent to the Alarm Receiver
        final Intent rec_intent = new Intent(this.context,Alarm_Receiver.class);
        // initialize switch
        final Switch sb = findViewById(R.id.switchAlarm);


        // create on checked change listener
        sb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sb.setText("Alarm On");

                    // setting calendar instance with the hour and minutes that picked
                    calendar.set(Calendar.HOUR_OF_DAY,alarm_timePicker.getHour());
                    calendar.set(Calendar.MINUTE,alarm_timePicker.getMinute());

                    // get the string values of hour and minute
                    int hour = alarm_timePicker.getHour();
                    int minute = alarm_timePicker.getMinute();

                    // convert the int values to string
                    String hour_str = String.valueOf(hour);
                    String min_str = String.valueOf(minute);
                    if(minute<10)
                        min_str = "0" + String.valueOf(minute);
                    Log.e("ALARM", "Alarm set on: " + hour_str + ":" + min_str);

                    //create a pending intent that delays the intent
                    // until the specified calendar time
                    pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,0,rec_intent
                    ,PendingIntent.FLAG_UPDATE_CURRENT);

                    //set the alarm manager
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis()
                    ,pendingIntent);
                }else{
                    sb.setText("Alarm Off");

                    alarmManager.cancel(pendingIntent);
                }
            }
        });
    }
}
