package com.garry.runningmap.ui;

import android.app.Activity;

/**
 * Created by keybo on 2017/12/31 0031.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
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
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.garry.runningmap.dao.HistoryDao;
import com.garry.runningmap.entity.History;
import com.garry.runningmap.entity.Point;
import com.garry.runningmap.entity.Speed;
import com.garry.runningmap.utils.Utils;
import com.garry.runningmap.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.garry.runningmap.utils.Utils.secToFormat;

/**
 * Created by keybo on 2017/12/29 0029.
 */

public class DetailActivity extends Activity {
    //定位相关
    BitmapDescriptor mCurrentMarker;
    LatLng target;
    List<LatLng> latLngs = new ArrayList<LatLng>();
    LocationClient mLocationClient;
//    private MyLocationListener mMyLocationListener = new DetailActivity.MyLocationListener();

    private Button seeSpeeds;

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Marker mMarkerA;
    private Marker mMarkerB;
    private InfoWindow mInfoWindow;
    MapStatus.Builder builder;
    //跑步信息
    TextView time ;
    TextView speed;
    TextView distance ;

    //默认地图缩放比例值
    float mCurrentZoom = 20f;

    //起点、终点图标
    BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_startpoint);
    BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_finishpoint);


    //运动轨迹图层
    Polyline mPolyline;

    //每公里配速
    List<Speed> speeds = new ArrayList<>();


    private History history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        getData();//获得数据
        initView(history);
        toLoc();//定位设置地图

        seeSpeeds = (Button) findViewById(R.id.seeSpeeds);
        seeSpeeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this,SpeedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("speeds", (Serializable) speeds);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });



        //一个用来放权限名字字符串的list
        List<String> permissionList = new ArrayList<>();
        //检查权限是否已获得
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(DetailActivity.this, permissions, 1);
        }
        else {
            //如果有其中一个没有获得就申请权限
            requestLocation();
        }
    }

    private void toLoc() {
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
//        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
//                MyLocationConfiguration.LocationMode.FOLLOWING,false,null)
//        );

        builder = new MapStatus.Builder();
        builder.target(target).zoom(20f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        MarkerOptions oStart = new MarkerOptions();
        oStart.position(latLngs.get(0));
        oStart.icon(startBD);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(oStart));

        MarkerOptions oFinish = new MarkerOptions().position(latLngs.get(latLngs.size()-1)).icon(finishBD).zIndex(2);
        mMarkerB = (Marker) (mBaiduMap.addOverlay(oFinish));

        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(latLngs);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        mPolyline.setZIndex(3);
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
//        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//gps定位
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(3000);
        mLocationClient.setLocOption(option);
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        history = (History) bundle.getSerializable("history");
        HistoryDao historyDao = new HistoryDao(this);
        //配速
        speeds = historyDao.findSpeedsByid(history.getRun_id());
        String filename = "points"+history.getRun_id();
        //轨迹点
        latLngs =readByFile(filename);
        target = new LatLng(latLngs.get(0).latitude,latLngs.get(0).longitude);

    }


    private List<LatLng> readByFile(String filename) {
        FileInputStream fis = null;
        try{
            fis = openFileInput(filename);
            if (fis.available() == 0){
                Toast.makeText(getApplicationContext(),"can not find file",Toast.LENGTH_SHORT).show();
                fis.close();
                return null;
            }

            InputStreamReader inputreader = new InputStreamReader(fis);
            BufferedReader buffreader = new BufferedReader(inputreader);
                String strLine = null;
                List<LatLng> latLngs = new ArrayList<LatLng>();
                while ((strLine = buffreader.readLine())!=null){
                    double lat = Double.parseDouble(strLine.split(" ")[0]);
                    double lo = Double.parseDouble(strLine.split(" ")[1]);
                    LatLng latLng = new LatLng(lat,lo);
                    latLngs.add(latLng);
                }
                return latLngs;



        } catch (FileNotFoundException e) {
            Log.d("TestFile", "The File doesn't not exist.");
        } catch (IOException e) {
            Log.d("TestFile", e.getMessage());
        }
        return null;
    }


    private void initView(History history) {

        time = (TextView) findViewById(R.id.time);
        speed = (TextView) findViewById(R.id.speed);
        distance = (TextView) findViewById(R.id.distance);

        time.setText(history.getTime());
        speed.setText(history.getAveSpeed());
        distance.setText(history.getLen());
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
//        mLocationClient.unRegisterLocationListener(mMyLocationListener);
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

}
