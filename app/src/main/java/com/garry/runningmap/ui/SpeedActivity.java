package com.garry.runningmap.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.garry.runningmap.R;

import com.garry.runningmap.entity.Speed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by keybo on 2018/1/1 0001.
 */

public class SpeedActivity extends Activity{
    private List<Speed> speeds;
    private int length;
    private ListView listView;
    private List<Map<String,Object>> data;//数据源
    private Map<String,Object> item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speeds);
        getData();//获取数据
        listView = (ListView) findViewById(R.id.list);


        data = new ArrayList<Map<String,Object>>();
        for (int i = 0 ;i <length;i++){
            item = new HashMap<String,Object>();
            item.put("position",i+1);
            item.put("speed",speeds.get(i).getSpeed());
            data.add(item);
        }

        SimpleAdapter listAdapter;
        listAdapter = new SimpleAdapter(SpeedActivity.this,data,R.layout.item_speed,
                new String[] {"position","speed"},
                new int[] {R.id.position,R.id.speed});
        listView.setAdapter(listAdapter);
        listView.setEnabled(false);

    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        speeds = (List<Speed>) bundle.getSerializable("speeds");
        if (speeds == null || speeds.size() == 0){
            Toast.makeText(SpeedActivity.this,"无配速详情",Toast.LENGTH_SHORT).show();
        }else {
            length = speeds.size();
        }




    }
}
