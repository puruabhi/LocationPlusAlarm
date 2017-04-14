package com.example.abhisheikh.locationplusalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class AlarmListActivity extends AppCompatActivity {

    private RecyclerView alarmRecyclerView;
    private List<Alarm> alarmList = new ArrayList<>();
    private AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        alarmRecyclerView = (RecyclerView)findViewById(R.id.alarmRecyclerView);

        alarmAdapter = new AlarmAdapter(this, alarmList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        alarmRecyclerView.setLayoutManager(layoutManager);
        alarmRecyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(alarmRecyclerView.getContext(),
                layoutManager.getOrientation());
        alarmRecyclerView.addItemDecoration(dividerItemDecoration);
        alarmRecyclerView.setAdapter(alarmAdapter);

        prepareAlarmData();

    }

    private void prepareAlarmData(){
        alarmList.add(new Alarm(new LatLng(29.869419, 77.894877),
                "Ravi","Get well soon",30, 0, false, false));
        alarmList.add(new Alarm(new LatLng(29.870026, 77.895118),
                "Bharat","Get well soon",30, 0, false, false));
        alarmList.add(new Alarm(new LatLng(29.8699363,77.8946516),
                "Purushottam","Get well soon",30, 0, false, false));

        alarmAdapter.notifyDataSetChanged();
    }
}
