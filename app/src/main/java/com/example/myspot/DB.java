package com.example.myspot;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public abstract class DB {
    private final static String DB_NAME = "spots.db";
    private static SQLiteDatabase db;
    private static ArrayList<Parking> spots = new ArrayList<>();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static void createAndOrLoadDB(Context baseContext){
        db = baseContext.openOrCreateDatabase(getDbName(), Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS parking (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
                "latitude REAL NOT NULL," +
                "longitude REAL NOT NULL," +
                "initialCost REAL NOT NULL," +
                "totalCost REAL NOT NULL," +
                "time TEXT NOT NULL," +
                "duration INTEGER NOT NULL," +
                "alarm INTEGER NOT NULL," +
                "active INTEGER NOT NULL" +
                ");");

        readDB();
    }

    //Updates spots array with current data from table "parking"
    private static void readDB() {
        Cursor cursor = db.rawQuery("SELECT " +
                                    //columnIndex
                "latitude, " +      //0
                "longitude, " +     //1
                "initialCost, " +   //2
                "totalCost, " +     //3
                "time, " +          //4
                "duration, " +      //5
                "alarm, " +         //6
                "active " +         //7
                "FROM parking;", null);

        while (cursor.moveToNext()){
            //parse time string from database to Calendar object
            Calendar calendar = new GregorianCalendar();

            try {
                calendar.setTime(simpleDateFormat.parse(cursor.getString(4)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //create new instances of parking objects from database and add them to spots list
            spots.add(new Parking(
                    new LatLng(cursor.getDouble(0),cursor.getDouble(1)),//location: columns latitude, longitude
                    cursor.getDouble(2),//initialCost: column initialCost
                    cursor.getDouble(3),//totalCost: column finalCost
                    calendar,//time: column time parsed to Calendar object
                    cursor.getInt(5),//duration: column duration
                    cursor.getInt(6) == 1,//alarm: column alarm parsed to boolean
                    cursor.getInt(7) == 1//active: column active parse to boolean
            ));
        }

        cursor.close();
    }

    public static void addParking(Parking parking){
        spots.add(parking);

        db.execSQL("INSERT INTO parking(" +
                "\"latitude\",\"longitude\",\"initialCost\",\"totalCost\",\"time\",\"duration\",\"alarm\",\"active\"" +
                ") " +
                "VALUES " +
                "(" +
                parking.getLocation().latitude +
                "," + parking.getLocation().longitude +
                "," + parking.getInitialCost() +
                "," + parking.getFinalCost() +
                ",\"" + simpleDateFormat.format(parking.getTime().getTime()) + "\"" +
                "," + parking.getDuration() +
                "," + (parking.isAlarm()?1:0) +
                "," + (parking.isActive()?1:0) +
                ");");
    }

    public static String getDbName() {
        return DB_NAME;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }
}
