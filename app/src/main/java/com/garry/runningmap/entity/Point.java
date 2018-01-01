package com.garry.runningmap.entity;

import java.io.Serializable;

/**
 * Created by keybo on 2017/12/30 0030.
 */

public class Point implements Serializable {
    int point_id;
    double lat;
    double lon;
    int run_id;

    public Point(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    public int getRun_id() {
        return run_id;
    }

    public int getPoint_id() {
        return point_id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setRun_id(int run_id) {
        this.run_id = run_id;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setPoint_id(int point_id) {
        this.point_id = point_id;
    }
}
