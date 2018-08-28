package com.example.user.grafacc;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager sensorManager;
    List<Sensor> sensorList=new ArrayList<>();


    SensorAdapter sensorAdapter;

    Sensor sensorAccelerometr;
    Sensor sensorGiroscope;
    Sensor sensorMagnitometr;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensorAccelerometr = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGiroscope=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorMagnitometr=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorList.add(sensorAccelerometr);
        sensorList.add(sensorGiroscope);
        sensorList.add(sensorMagnitometr);

        sensorAdapter=new SensorAdapter(this,sensorList);
        recyclerView.setAdapter(sensorAdapter);
        sensorAdapter.notifyDataSetChanged();
    }

}
