package com.garry.runningmap.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.garry.runningmap.utils.Utils;
import com.garry.runningmap.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.garry.runningmap.utils.Utils.secToFormat;

/**
 * Created by keybo on 2017/12/29 0029.
 */

public class RunActivity extends Activity {
    private String title;

    //定位相关
    LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener = new RunActivity.MyLocationListener();

    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;

    private MapView mMapView;
    private BaiduMap mBaiduMap;


    //跑步信息
    TextView time ;
    TextView speed;
    TextView distance ;
    Button finish;
    //计时
    private int curTime = 0;
    //距离
    double distance_len;
    //配速
    double speed_double;

    //是否首次定位
    private Boolean isFirst = true;
    private MyLocationData locData;
    //默认地图缩放比例值
    float mCurrentZoom = 20f;

    //起点、终点图标
    BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_startpoint);
    BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_finishpoint);

    //位置点集合
    List<LatLng> points = new ArrayList<LatLng>();
    //运动轨迹图层
    Polyline mPolyline;
    //上一个定位点
    LatLng lastLoc = new LatLng(0,0);
    MapStatus.Builder builder;

    //每公里配速
    List <String> speeds = new ArrayList<>();
    List<Integer> speeds_int = new ArrayList<Integer>();
    //当前公里整型
    int curMile = 0;
    //标志公里配速
    boolean[] flag = new boolean[51];
    int lastTime=0;//上一公里的时间



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_run);
        Intent intent =getIntent();
        Bundle bundle = intent.getExtras();
        title = bundle.getString("title");

        initView();
        new Thread(new TimeThread()).start();
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING,false,null)
        );

        /**
         * 添加地图缩放状态变化监听，当手动缩放地图时，拿到缩放后的比例，然后获取到下次定位，
         * 给地图重新设置缩放比例
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                mCurrentZoom = mapStatus.zoom;
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

            }
        });

        //定位初始化
        mLocationClient= new LocationClient(this);
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//gps定位
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(3000);
        mLocationClient.setLocOption(option);

        //一个用来放权限名字字符串的list
        List<String> permissionList = new ArrayList<>();
        //检查权限是否已获得
        if (ContextCompat.checkSelfPermission(RunActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(RunActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(RunActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(RunActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(RunActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(RunActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            //如果有其中一个没有获得就申请权限
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(RunActivity.this, permissions, 1);
        }
        else {

            requestLocation();
        }


    }




    private void initView() {
        finish = (Button) findViewById(R.id.finish);
        time = (TextView) findViewById(R.id.time);
        speed = (TextView) findViewById(R.id.speed);
        distance = (TextView) findViewById(R.id.distance);




        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder builder= new AlertDialog.Builder(RunActivity.this);
                    builder.setIcon(R.drawable.icon_geo);

                    builder.setTitle("退出");
                    if (distance_len<0.1) {
                        builder.setMessage("确认结束？跑步距离太短，将不会保存此次记录");

                    }else
                        builder.setMessage("确认结束?");
                    builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(RunActivity.this, "继续",Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mLocationClient != null && mLocationClient.isStarted()) {
                                mLocationClient.stop();

                                if (isFirst) {
                                    points.clear();
                                    lastLoc = new LatLng(0, 0);
                                    return;
                                }

                                MarkerOptions oFinish = new MarkerOptions();//地图标记覆盖物参数配置
                                oFinish.position(points.get(points.size() - 1));
                                oFinish.icon(finishBD);
                                mBaiduMap.addOverlay(oFinish);
                            }
                            if (distance_len>0.1){
                                // 大于一百米保存
                                // 保存到数据库
                            if (speeds_int.size() ==0 ){
                                //小于一公里
                                speeds_int.add((int) (curTime/distance_len));
                                int curSpeedMin = (int) (curTime/distance_len/60);
                                int curSpeedSec = (int) (curTime/distance_len%60);
                                speeds.add(curSpeedMin+"'"+String.format("%02d",curSpeedSec)+"''");
                            }

                                Toast.makeText(RunActivity.this, RunActivity.this.getFilesDir().toString(),Toast.LENGTH_SHORT);
                                String filename=Utils.save(RunActivity.this,points, curTime,distance_len,speeds,speeds_int,title);
                                //保存points到文件
                                save(filename);

                            }


                            //复位
                            speeds = new ArrayList<>();
                            points.clear();
                            lastLoc = new LatLng(0,0);
                            isFirst = true;
                            finish();

                        }
                    });
                    AlertDialog b=builder.create();
                    b.show();

                }

        });

    }

    private void save(String fileName) {
        Toast.makeText(RunActivity.this,"保存",Toast.LENGTH_SHORT);

        FileOutputStream fos = null;

        try{
            fos=openFileOutput(fileName,MODE_APPEND);
            for (int i =0;i<points.size();i++){
                String str = points.get(i).latitude+" "+points.get(i).longitude+"\n";
                fos.write(str.getBytes());

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //请求权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "需要同意所有权限才可运行本程序", LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    private void requestLocation(){
        mLocationClient.start();
    }





    //自定义定位监听器
    private class MyLocationListener implements BDLocationListener {
        @Override    //获取当前位置经纬度
        public void onReceiveLocation(BDLocation bdLocation) {

            String addr = bdLocation.getAddrStr();    //获取详细地址信息
            if (bdLocation == null || mMapView == null)
                return;

           if (isFirst){
                LatLng ll = null;

               ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());

                if (ll == null){
                    return;
                }

                isFirst = false;
                points.add(ll);
                lastLoc =ll;

                //显示当前定位点，缩放地图
               locateAndZoom(bdLocation,ll);

                //标记起点图层位置
               MarkerOptions oStart = new MarkerOptions();
               oStart.position(points.get(0));
               oStart.icon(startBD);
               mBaiduMap.addOverlay(oStart);
               return;
            }


            //从第二个点开始
            LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());


           //回调gps位置的频率是3秒一个
            if(DistanceUtil.getDistance(lastLoc,ll) > 5 )
                //大于5米才加进去
                points.add(ll);
            //计算距离
            distance_len = Utils.calculateDistance(points);
            distance.setText(String.format("%.2f",distance_len)+" km");

            //计算3秒配速
            List<LatLng> recent = new ArrayList<LatLng>() ;
            recent.add(lastLoc);
            recent.add(ll);
            double curLen = Utils.calculateDistance(recent);
            double avespeed = 3.0/curLen;
            int speed_min= (int) (avespeed/60);
            double speed_sec = curLen%60.0;
            if(speed_min>20){
                //认为没有在运动
                speed.setText("--:--");
            }else {
                speed.setText(speed_min + "'" + String.format("%02d", speed_sec) + "''");
            }
            //每到一公里 记录配速 speed_int用来算平均配速
            if (distance_len >= (curMile+1) && !flag[curMile+1]){
                curMile++;
                flag[curMile] = true;
                int curSpeedMin = (curTime -lastTime)/60;
                int curSpeedSec = (curTime -lastTime)%60;
                speeds.add(curSpeedMin+"'"+String.format("%02d",curSpeedSec)+"''");
                speeds_int.add(curTime-lastTime);

                lastTime= curTime;

            }

            lastLoc = ll;
            //显示当前定位点，缩放地图
            locateAndZoom(bdLocation,ll);

            //清除上一次轨迹，避免重叠绘画
            mMapView.getMap().clear();

            //起始点图层也会被清除，重新绘画
            MarkerOptions oStart = new MarkerOptions();
            oStart.position(points.get(0));
            oStart.icon(startBD);
            mBaiduMap.addOverlay(oStart);

            //将points集合中的点绘制轨迹线条图层，显示在地图上
            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(points);
            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        }
    }

    private void locateAndZoom(BDLocation bdLocation, LatLng ll) {
        this.mCurrentLat = bdLocation.getLatitude();
        this.mCurrentLon = bdLocation.getLongitude();
        locData = new MyLocationData.Builder().accuracy(0).latitude(mCurrentLat)
                .longitude(mCurrentLon).build();
        mBaiduMap.setMyLocationData(locData);
        //Toast.makeText(this, bdLocation.getAddrStr()+"", LENGTH_SHORT).show();

        builder = new MapStatus.Builder();
        builder.target(ll).zoom(mCurrentZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }




    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.start();

    }

    //管理地图的生命周期
    @Override
    protected void onResume() {

        super.onResume();
        mMapView.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
        mMapView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(mMyLocationListener);
        if (mLocationClient != null && mLocationClient.isStarted()){
            mLocationClient.stop();
        }
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.getMap().clear();
        mMapView.onDestroy();
        mMapView = null;
        startBD.recycle();
        finishBD.recycle();



    }
    final Handler handler = new Handler(){          // handle
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 1:
                    curTime++;
                    String cur = secToFormat(curTime);
                    time.setText(cur);
            }
            super.handleMessage(msg);
        }
    };


    private class TimeThread implements Runnable {
        @Override
        public void run() {
            while (true){
                try{
                    Thread.sleep(1000);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }catch (Exception e){

                }

            }
        }
    }
}
