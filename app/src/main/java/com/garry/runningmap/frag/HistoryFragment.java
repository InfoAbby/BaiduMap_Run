package com.garry.runningmap.frag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.garry.runningmap.MainActivity;
import com.garry.runningmap.R;
import com.garry.runningmap.dao.HistoryDao;
import com.garry.runningmap.entity.History;
import com.garry.runningmap.entity.Speed;
import com.garry.runningmap.ui.DetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by keybo on 2017/12/29 0029.
 */

public class HistoryFragment extends Fragment {
    private ListView listView;
    private List<Map<String,Object>> data;
    private Map<String,Object> item;

    private TextView title;

    private HistoryDao historyDao;
    private int length =0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);

        Bundle bundle = getArguments();
        final List<History> historyList = (List<History>) bundle.getSerializable("history");
        if (historyList == null || historyList.size()==0)
            Toast.makeText(getActivity().getApplicationContext(),"无记录，快去运动吧！",Toast.LENGTH_SHORT).show();
        else{
            length = historyList.size();
        }
        listView = (ListView) view.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent  = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("history",historyList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //数据源
        data = new ArrayList<Map<String,Object>>();
        for (int i = 0;i <length;i++){
            item = new HashMap<String,Object>();
            if (historyList.get(i).getTitle().equals("run"))
                item.put("title","户外跑步");
            else if (historyList.get(i).getTitle().equals("bike"))
                item.put("title","户外骑行");
            item.put("day",historyList.get(i).getDay());
            item.put("curTime",historyList.get(i).getCurTime());
            item.put("timg",historyList.get(i).getTime());
            item.put("speed",historyList.get(i).getAveSpeed());
            item.put("run_id",historyList.get(i).getRun_id());
            item.put("len",historyList.get(i).getLen());
            data.add(item);
        }

        //生成简单适配器
        SimpleAdapter listAdapter;
        listAdapter = new SimpleAdapter(getActivity().getApplicationContext(),data, R.layout.item,
                new String[] {"run_id","day","title","len","curTime","timg","speed"},
                new int[] {R.id.run_id,R.id.day,R.id.title,R.id.len,R.id.curtime,R.id.time,R.id.speed});
        listView.setAdapter(listAdapter);
        return view;

    }




}
