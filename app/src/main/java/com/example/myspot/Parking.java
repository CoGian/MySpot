package com.example.myspot;


import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class Parking {
    private LatLng location;
    private double initialCost;
    private double finalCost;
    private Calendar time;
    private int duration;
    private boolean alarm;
    private boolean active;

    public Parking(LatLng location, double initialCost, double finalCost, Calendar time, int duration, boolean alarm, boolean active) {
        this.location = location;
        this.initialCost = initialCost;
        this.finalCost = finalCost;
        this.time = time;
        this.duration = duration;
        this.alarm = alarm;
        this.active = active;
    }


}
