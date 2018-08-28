package com.example.user.grafacc;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GrafActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    Sensor sensor;
    GraphView graph;
    private double graph2LastXValue = 5d;
    private double graph2LastYValue = 5d;
    private double graph2LastZValue = 5d;
    private Double[] dataPoints;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> seriesX;
    LineGraphSeries<DataPoint> seriesZ;
    private Thread thread;
    private boolean plotData = true;

    Button startRecord;
    Button stopRecord;
    Button showRecord;
    TextView recordResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        startRecord = (Button) findViewById(R.id.start_record);
        stopRecord = (Button) findViewById(R.id.stop_record);
        showRecord = (Button) findViewById(R.id.show_record);
        recordResult = (TextView) findViewById(R.id.record_result);

        setSupportActionBar(toolbar);

        Bundle extras=getIntent().getExtras();
        if(extras!=null)
        {
          System.out.println(extras.getInt("sensortype"));
        }


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=mSensorManager.getDefaultSensor(extras.getInt("sensortype"));
        System.out.println(sensor);
        graph=(GraphView)findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 0),
        });
        series.setColor(Color.GREEN);

        seriesX=new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0),

        });
        seriesX.setColor(Color.BLACK);

        seriesZ=new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0,0),
        });
        seriesZ.setColor(Color.RED);

        graph.addSeries(seriesX);
        graph.addSeries(series);
        graph.addSeries(seriesZ);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);
        feedMultiple();

    }
    public void addEntry(SensorEvent event) {
        /*     LineGraphSeries<DataPoint> series = new LineGraphSeries<>();*/
        float[] values = event.values;
        // Movement
        float x = values[0];
        System.out.println(x);
        float y = values[1];
        System.out.println(y);
        float z = values[2];
        System.out.println(z);


        graph2LastXValue += 1d;
        graph2LastYValue += 1d;
        graph2LastZValue += 1d;
        series.appendData(new DataPoint(graph2LastYValue, y), true, 20);

        seriesX.appendData(new DataPoint(graph2LastXValue, x), true, 20);
        seriesZ.appendData(new DataPoint(graph2LastZValue, z), true, 20);
        graph.addSeries(series);
        graph.addSeries(seriesX);
        graph.addSeries(seriesZ);

    }
    private void addDataPoint(double acceleration) {
        dataPoints[499] = acceleration;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(plotData){
            addEntry(event);

            plotData = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(GrafActivity.this);
        thread.interrupt();
        super.onDestroy();
    }
}
