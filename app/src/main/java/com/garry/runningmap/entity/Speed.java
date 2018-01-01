package com.garry.runningmap.entity;

import java.io.Serializable;

/**
 * Created by keybo on 2017/12/30 0030.
 */

public class Speed implements Serializable {
    int speed_id;
    int run_id;
    String speed;
    public Speed(){

    }
    public Speed(String s) {
        this.speed = s;
    }

    public int getRun_id() {
        return run_id;
    }

    public void setRun_id(int run_id) {
        this.run_id = run_id;
    }

    public int getSpeed_id() {
        return speed_id;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setSpeed_id(int speed_id) {
        this.speed_id = speed_id;
    }

}

