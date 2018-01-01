package com.garry.runningmap.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by keybo on 2017/12/30 0030.
 */

public class MyDBOpenHelper extends SQLiteOpenHelper {
    public MyDBOpenHelper(Context context) {
        super(context, "run.db", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE history (run_id  integer primary key autoincrement,  " +
                "time varchar(20) not null," +
                "len varchar(20) not null," +
                "addr varchar(20) not null," +
                "day varchar(20) not null ," +
                "rectime varchar(20) not null," +
                "avespeed varchar(20) not null," +
                "title varchar(20) not null); ");

        db.execSQL("CREATE TABLE speeds(speed_id integer primary key autoincrement," +
                "run_id integer," +
                "speed varchar(20) not null," +
                "foreign key(run_id) REFERENCES  history(run_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS history;" );
        db.execSQL("DROP TABLE IF EXISTS speeds;" );
        onCreate(db);

    }
}
