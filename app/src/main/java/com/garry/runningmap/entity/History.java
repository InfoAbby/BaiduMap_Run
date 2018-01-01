package com.garry.runningmap.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by keybo on 2017/12/30 0030.
 */

public class History implements Serializable{
    private int run_id;
    private String time;
    private String len;
    private String addr;
    private String day;
    private String curTime;
    private String aveSpeed;//平均配速
    private String title;//run bike

    public History() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public String getAveSpeed() {
        return aveSpeed;
    }

    public void setAveSpeed(String aveSpeed) {
        this.aveSpeed = aveSpeed;
    }


    public History(String time, String len, String title, Speed aveSpeed){
        this.time=time;
        this.len=len;this.title=title;
        this.aveSpeed=aveSpeed.getSpeed();

    }
    public String getCurTime() {
        return curTime;
    }

    public String getDay() {
        return day;
    }

    public void setCurTime(String curTime) {
        this.curTime = curTime;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getRun_id() {
        return run_id;
    }

    public void setRun_id(int run_id) {
        this.run_id = run_id;
    }

    public void setLen(String len) {
        this.len = len;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLen() {
        return len;
    }

    public String getTime() {
        return time;
    }
}
