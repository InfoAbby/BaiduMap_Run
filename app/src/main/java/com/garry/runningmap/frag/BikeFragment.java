package com.garry.runningmap.frag;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.garry.runningmap.R;
import com.garry.runningmap.ui.RunActivity;

/**
 * Created by keybo on 2017/12/29 0029.
 */

public class BikeFragment extends Fragment {
    private Intent intent;
    private Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bike,container,false);
        button = (Button) view.findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(),RunActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title","bike");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }
}
