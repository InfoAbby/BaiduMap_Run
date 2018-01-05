package com.garry.runningmap.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.garry.runningmap.entity.History;
import com.garry.runningmap.entity.Speed;
import com.garry.runningmap.utils.MyDBOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by keybo on 2017/12/30 0030.
 */

public class HistoryDao {
    private static final String DB = "run.db"; //数据库名
    private static final String DB_TABLE = "run"; //表名

    private SQLiteDatabase db;
    private final Context context;
    private MyDBOpenHelper dbOpenHelper;

    public HistoryDao(Context context){

        this.context = context;
        dbOpenHelper = new MyDBOpenHelper(this.context);
        db = dbOpenHelper.getWritableDatabase();

    }


    public void close(){
        if (db!=null){
            db.close();
            db = null;
        }
    }

    public void add(History history, List<Speed> speeds){
        addRun(history);
        addSpeeds(speeds);
    }

    public List<History> getAllRun(){
        try{
            db = dbOpenHelper.getWritableDatabase();
            String sql = "select * from history order by run_id desc";
            Cursor cursor = db.rawQuery(sql,null);
            List<History> historyList = new ArrayList<History>();
            while (cursor.moveToNext()){
                History history = new History();
                history.setRun_id(cursor.getInt(0));
                history.setTime(cursor.getString(1));
                history.setLen(cursor.getString(2));
                history.setAddr(cursor.getString(3));
                String day = cursor.getString(4);
                String recTime = cursor.getString(5);
                //时间读取 格式

                history.setDay(day);
                history.setCurTime(recTime);

                history.setAveSpeed(cursor.getString(6));
                history.setTitle(cursor.getString(7));
                historyList.add(history);
            }
            return historyList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }


    private void addSpeeds(List<Speed> speeds) {
        try {
            db= dbOpenHelper.getWritableDatabase();
            for (int i =0;i<speeds.size();i++){
                ContentValues values = new ContentValues();
                int run_id = getRun_Id();
                values.put("run_id",run_id);
                values.put("speed",speeds.get(i).getSpeed());
                Long row = db.insert("speeds",null,values);

            }
        }catch (Exception e){
            e.printStackTrace();

        }

    }




    public void addRun(History history) {
        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String day = dateFormat.format(now);
        dateFormat = new SimpleDateFormat("HH:mm");
        String curTime = dateFormat.format(now);
        try {

            db = dbOpenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("time", history.getTime());
            values.put("len", history.getLen());
            values.put("addr","points"+(getRun_Id()+1));
            values.put("day",day);
            values.put("rectime",curTime);
            values.put("avespeed",history.getAveSpeed());
            values.put("title",history.getTitle());
            Long row;
            row = db.insert("history",null,values);
            Log.v("row",row+"");
        }catch (Exception e){
            e.printStackTrace();

        }
    }


    public int getRun_Id() {
        int id = 0;
        Cursor cursor;
        try{
            cursor = db.rawQuery("select count(*) from history;",null);
            if (cursor.moveToNext()){
                id = cursor.getInt(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        Log.v("get",""+id);

        return id;
    }

    public String getAddress() {
        String str = "points"+getRun_Id();
        return str;
    }

    public List<Speed> findSpeedsByid(int run_id) {
        try{
            db = dbOpenHelper.getWritableDatabase();
            String sql = "select * from speeds where run_id = "+run_id;
            Cursor cursor = db.rawQuery(sql,null);
            List<Speed> speedList = new ArrayList<Speed>();
            while (cursor.moveToNext()){
                Speed speed = new Speed();

                speed.setRun_id(cursor.getInt(1));
                speed.setSpeed(cursor.getString(2));
                speedList.add(speed);
            }
            return speedList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
