package com.garry.runningmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import android.support.design.widget.NavigationView;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.mapapi.SDKInitializer;
import com.garry.runningmap.dao.HistoryDao;
import com.garry.runningmap.entity.History;
import com.garry.runningmap.frag.BikeFragment;
import com.garry.runningmap.frag.RunFragment;
import com.garry.runningmap.frag.HistoryFragment;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  BottomNavigationBar.OnTabSelectedListener {
    private String TAG = "main";
    private DrawerLayout mDrawerLayout;//侧滑栏

    private Fragment runFragment = new RunFragment();
    private Fragment historyFragment = new HistoryFragment();
    private Fragment bikeFragment = new BikeFragment();
    private BottomNavigationBar buttomBar ;

    private SDKReceiver mReceiver;

    public HistoryDao historyDao;
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();

            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(MainActivity.this,"apikey验证失败，地图功能无法正常使用",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Toast.makeText(MainActivity.this,"apikey验证成功",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // apikey的授权需要一定的时间，在授权成功之前地图相关操作会出现异常；apikey授权成功后会发送广播通知，我们这里注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);                    //侧滑栏
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);       //侧滑栏内容
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);                             //工具栏
        setSupportActionBar(toolbar);

        //底部控制器
        addButtomBar();

        init();

        //获取到工具栏，将默认的返回按钮显示并改变样式
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }




        //侧滑栏监听器，当发生点击事件时，将被点击的item返回到onNavigationItemSelected函数
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //点击用户头像时
                    case R.id.user_image:

                        break;
                    //点击“我的收藏”
                    case R.id.nav_collect:

                        break;
                    //点击“设置”
                    case R.id.nav_option:

                        break;
                    //点击“关于”
                    case R.id.nav_about:

                        break;
                    default:
                }
                return true;
            }
        });
    }

    private void init() {
       setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_main,runFragment);
        transaction.commit();

    }

    private void hideAllFrag() {
        hideFrag(runFragment);
        hideFrag(historyFragment);
    }

    private void hideFrag(Fragment frag) {
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if (frag!=null && frag.isAdded()){
//            ft.hide(frag);
//        }
//        ft.commit();
    }

    private void addButtomBar() {
        buttomBar = (BottomNavigationBar) findViewById(R.id.bottom_bar);
        buttomBar.setMode(BottomNavigationBar.MODE_SHIFTING_NO_TITLE);
        buttomBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        buttomBar.setBarBackgroundColor(R.color.colorAccent);
        buttomBar.setActiveColor(R.color.colorP);
        buttomBar.setInActiveColor(R.color.colorW);
        buttomBar.addItem(new BottomNavigationItem(R.drawable.run,"跑步"));
        buttomBar.addItem(new BottomNavigationItem(R.drawable.bike,"骑行"));
        buttomBar.addItem(new BottomNavigationItem(R.drawable.half,"设置")).setFirstSelectedPosition(0).initialise();
        buttomBar.setTabSelectedListener(this);
    }






    //工具栏点击事件，将工具栏中被点击的Item返回到onOptionsItemSelected函数
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //改变默认的返回按钮功能为唤出侧滑栏
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            //点击搜索按钮，弹出搜索框
            //case R.id.search:

                //break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }


    //在活动创建时载入工具栏
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public void onTabSelected(int i) {
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (i){
            case 0:
                transaction.replace(R.id.fragment_main,runFragment);
                break;
            case 1:
                transaction.replace(R.id.fragment_main,bikeFragment);
                break;
            case 2:

                Bundle bundle = getHistory();
                historyFragment.setArguments(bundle);
                transaction.replace(R.id.fragment_main, historyFragment);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private Bundle getHistory() {
        historyDao = new HistoryDao(MainActivity.this);
        List<History> historyList = historyDao.getAllRun();
        Bundle bundle = new Bundle();
        bundle.putSerializable("history", (Serializable) historyList);
        return bundle;
    }

    @Override
    public void onTabUnselected(int i) {
        Log.v(TAG, "onTabUnselected() called with: " + "position = [" + i + "]");

    }

    @Override
    public void onTabReselected(int i) {

    }



}
