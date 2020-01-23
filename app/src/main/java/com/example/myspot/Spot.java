package com.example.myspot;

public class Spot {

    private double latitude ;
    private double longitude ;
    private String time ;
    private String address ;
    public Spot() {
    }

    public Spot(double latitude, double longitude, String time, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
