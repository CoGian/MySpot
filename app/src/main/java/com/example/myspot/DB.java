package com.example.myspot;

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
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.ENGLISH);

    public static void createAndOrLoadDB(){
        db.execSQL("CREATE TABLE IF NOT EXISTS \"parking\" (\n" +
                "\t\"id\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                "\t\"latitude\"\tREAL NOT NULL,\n" +
                "\t\"longitude\"\tREAL NOT NULL,\n" +
                "\t\"initialCost\"\tREAL,\n" +
                "\t\"totalCost\"\tREAL,\n" +
                "\t\"time\"\tTEXT NOT NULL,\n" +
                "\t\"duration\"\tINTEGER,\n" +
                "\t\"alarm\"\tINTEGER,\n" +
                "\t\"active\"\tINTEGER NOT NULL\n" +
                ")");

        readDB();
    }

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
                "FROM parking", null);

        while (cursor.moveToNext()){
            //parse time string from database to Calendar object
            Calendar calendar = new GregorianCalendar();

            try {
                calendar.setTime(simpleDateFormat.parse(cursor.getString(4)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //create new instances o parking objects from database and add them to spots list
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

    public static void setDb(SQLiteDatabase database) {
        db = database;
    }

    public static String getDbName() {
        return DB_NAME;
    }
}
