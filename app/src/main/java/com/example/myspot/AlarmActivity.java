package com.example.myspot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static java.lang.Math.floor;

public class AlarmActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private TimePicker alarm_timePicker ;
    private Context context;
    private PendingIntent pendingIntent;
    private Calendar calendar;
    private Intent rec_intent;
    private Switch sb ;
    private EditText initialCostText ;
    private EditText costPerHourText;
    private TextView finalCostView ;
    private  int hour ;
    private  int minute ;
    private  float cost_per_hour ;
    private  float initial_cost ;
    private  boolean alarmOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Toolbar alarmToolbar = findViewById(R.id.alarmToolbar);
        setSupportActionBar(alarmToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialCostText = findViewById(R.id.editTextInitialCost);
        costPerHourText = findViewById(R.id.editTextCostPerHour);
        finalCostView = findViewById(R.id.finalCostView);

        this.context = this ;
        // initialize alarm manager
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        // initialize timepicker
        alarm_timePicker = findViewById(R.id.alarmTimePicker);

        // create an intent to the Alarm Receiver
        rec_intent = new Intent(this.context,Alarm_Receiver.class);

        // initialize switch
        sb = findViewById(R.id.switchAlarm);

        // restore vars from savedInstance id exists
        if(savedInstanceState != null){
            hour = savedInstanceState.getInt("hour");
            minute = savedInstanceState.getInt("minute");
            initial_cost = savedInstanceState.getFloat("initial_cost");
            cost_per_hour = savedInstanceState.getFloat("cost_per_hour");
            alarmOn = savedInstanceState.getBoolean("alarmOn");

            initialCostText.setText(String.valueOf(initial_cost));
            costPerHourText.setText(String.valueOf(cost_per_hour));

            // calculate final cost
            double final_cost = calc_final_cost();
            finalCostView.setText("Final cost: "+final_cost+" €");
            if (alarmOn)
                sb.setText("Alarm On");
            else
                sb.setText("Alarm Off");


        }

        // create on checked change listener
        sb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !alarmOn){
                    sb.setText("Alarm On");
                    alarmOn = true;

                    // calculate final cost
                    double final_cost = calc_final_cost();

                    // get the string values of hour and minute
                    hour = alarm_timePicker.getHour();
                    minute= alarm_timePicker.getMinute();


                    rec_intent.putExtra("final_cost",final_cost);

                    // convert the int values to string
                    String hour_str = String.valueOf(hour);
                    String min_str = String.valueOf(minute);
                    if(minute<10)
                        min_str = "0" + String.valueOf(minute);
                    Log.e("ALARM", "Alarm set on: " + hour_str + ":" + min_str + "   on " + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+ " with cost: " + final_cost);

                    //create a pending intent that delays the intent
                    // until the specified calendar time
                    pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,0,rec_intent
                    ,PendingIntent.FLAG_UPDATE_CURRENT);

                    //set the alarm manager
                    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis()
                    ,pendingIntent);

                    Toast.makeText(context,"Alarm is on.", Toast.LENGTH_SHORT).show();
                }else if(!isChecked && alarmOn){

                    pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,0,rec_intent
                            ,PendingIntent.FLAG_UPDATE_CURRENT);

                    sb.setText("Alarm Off");
                    alarmOn = false;
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(context,"Alarm is off.", Toast.LENGTH_SHORT).show();
                    Log.e("ALARM", " ia m here");
                }

            }
        });

        // create on text changed listener
        initialCostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // calculate final cost
                double final_cost = calc_final_cost();
                finalCostView.setText("Final cost: "+final_cost+" €");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        costPerHourText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // calculate final cost
                double final_cost = calc_final_cost();
                finalCostView.setText("Final cost: "+final_cost+" €");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alarm_timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // calculate final cost
                double final_cost = calc_final_cost();
                finalCostView.setText("Final cost: "+final_cost+" €");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_menu, menu);
        return true;
    }

    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("hour", hour);
        outState.putInt("minute",minute);
        outState.putFloat("cost_per_hour",cost_per_hour);
        outState.putFloat("initial_cost",initial_cost);
        outState.putBoolean("alarmOn",alarmOn);


    }

    public double calc_final_cost(){
        // setting initial cost and cost per hour
        float initial_cost = 0 ;

        if (!initialCostText.getText().toString().equals(""))
            initial_cost =  Float.parseFloat(initialCostText.getText().toString()) ;

        float cost_per_hour = 0 ;

        if (!costPerHourText.getText().toString().equals(""))
            cost_per_hour = Float.parseFloat(costPerHourText.getText().toString() );

        // create instances of calendars
        calendar = Calendar.getInstance();
        Calendar curr_cal = Calendar.getInstance();
        // setting calendar instance with the hour and minutes that picked
        calendar.set(Calendar.HOUR_OF_DAY,alarm_timePicker.getHour());
        calendar.set(Calendar.MINUTE,alarm_timePicker.getMinute());
        calendar.set(Calendar.SECOND,0);
        Log.e("ALARM","" + calendar.getTimeInMillis()+ "     " + curr_cal.getTimeInMillis());
        // check if user has put time from tomorrow and increase calendars' day by one
        if (curr_cal.getTimeInMillis()>calendar.getTimeInMillis())
            calendar.add(Calendar.DAY_OF_YEAR,1);

        // calculate final cost
        double final_cost = initial_cost + floor((calendar.getTimeInMillis()-curr_cal.getTimeInMillis())/ 3600000 ) * cost_per_hour;

        return final_cost;
    }





}
