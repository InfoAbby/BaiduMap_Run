package com.garry.runningmap.utils;

import android.content.Context;
import android.widget.ListView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.garry.runningmap.dao.HistoryDao;
import com.garry.runningmap.entity.History;
import com.garry.runningmap.entity.Speed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keybo on 2017/12/30 0030.
 */

public class Utils {
    /**
     * 返回points文件名
     * @param context
     * @param points
     * @param recLen
     * @param distance_len
     * @param speeds
     * @param speeds_int
     * @param title
     * @return
     */
    public static String save(Context context, List<LatLng> points, int recLen, double distance_len, List<String> speeds, List<Integer> speeds_int, String title){
        String time = secToFormat(recLen);
        String len=String.format("%.2f",distance_len)+" km";
        //封装
        Speed aveSpeed = calculateAve(speeds_int);
        History history = new History(time,len,title,aveSpeed);

        List<Speed> speedList = new ArrayList<Speed>();
        for (int i=0;i<speeds.size();i++){
            Speed speed = new Speed(speeds.get(i));
            speedList.add(speed);
        }


        HistoryDao historyDao = new HistoryDao(context);

        historyDao.add(history,speedList);

        return historyDao.getAddress();
    }

    //计算平均配速
    public static Speed calculateAve(List<Integer> speeds){
        int sum = 0;
        for (int i = 0;i<speeds.size();i++){
            sum += speeds.get(i);
        }
        int ave = sum/speeds.size();
        int speed_min = ave/60;
        int speed_sec = ave%60;

        Speed aveSpeed = new Speed(speed_min+"'"+String.format("%02d",speed_sec)+"''");
        return aveSpeed;
    }




    public static String  secToFormat(int sec){
        String str_min,str_sec,str_hour;
        int min = sec/60;
        int hour = min/60;
        min = min-hour*60;
        sec = sec - hour*60-min*60;

        if (min==0){
            str_min = "00";
        }else if (min<10)
            str_min = "0"+min;
        else str_min = min+"";

        if (hour==0){
            str_hour = "00";

        }else if (hour<10)
            str_hour = "0"+hour;
        else str_hour = hour+"";

        if (sec==0){
            str_sec = "00";

        }else if (sec<10)
            str_sec = "0"+sec;
        else str_sec = sec+"";

        return str_hour+":"+str_min+":"+str_sec;
    }
    public static double calculateDistance(List<LatLng> points) {
        if (points.size() == 0)
            return 0.0;
        double distance = 0.0;
        for (int i = 0 ;i <points.size()-1;i++){
            double cur = DistanceUtil. getDistance(points.get(i), points.get(i + 1));
            distance += cur;
        }

        return distance/1000.0;
    }
}
