package com.garry.runningmap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWNaviStatusListener;
import com.baidu.platform.comapi.walknavi.WalkNaviModeSwitchListener;

public class NavigationActivity extends Activity {
    private WalkNavigateHelper mNaviHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取诱导页面地图展示View
        mNaviHelper = WalkNavigateHelper.getInstance();                                     //获取导航控制类
        try {
            View view = mNaviHelper.onCreate(NavigationActivity.this);

            if (view != null) {
                setContentView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mNaviHelper.setWalkNaviStatusListener(new IWNaviStatusListener() {
            @Override
            public void onWalkNaviModeChange(int mode, WalkNaviModeSwitchListener listener) {
                Toast.makeText(NavigationActivity.this, "导航模式发生改变", Toast.LENGTH_SHORT).show();
                mNaviHelper.switchWalkNaviMode(NavigationActivity.this, mode, listener);
            }

            @Override
            public void onNaviExit() {
                Toast.makeText(NavigationActivity.this, "退出导航", Toast.LENGTH_SHORT).show();
            }
        });
        // 开始导航
        mNaviHelper.startWalkNavi(NavigationActivity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

}
