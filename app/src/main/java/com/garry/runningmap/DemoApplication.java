package com.garry.runningmap;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by keybo on 2017/12/29 0029.
 */

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //设置坐标类型
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
