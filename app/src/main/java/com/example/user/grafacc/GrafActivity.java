package com.example.user.grafacc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private boolean state;
    private int timer = 0;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private final static String FILE_NAME = "filename.txt";
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private boolean permissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graf);
        state = false;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        startRecord = (Button) findViewById(R.id.start_record);
        stopRecord = (Button) findViewById(R.id.stop_record);
        showRecord = (Button) findViewById(R.id.show_record);
        recordResult = (TextView) findViewById(R.id.record_result);
        deleteFile();

        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = true;
            }
        });

        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = false;
            }
        });

        showRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = false;
                openText();
            }
        });
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println(extras.getInt("sensortype"));
        }


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(extras.getInt("sensortype"));
        System.out.println(sensor);
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        series.setColor(Color.GREEN);

        seriesX = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0),

        });
        seriesX.setColor(Color.BLACK);

        seriesZ = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0),
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

        if (state) {
            timer++;
            if (timer % 5 == 0) {
                System.out.println(timer);
                saveText(event);
            }
        }


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

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
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
        if (plotData) {
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

    public void deleteFile() {
        if (!permissionGranted) {
            checkPermissions();
            return;
        }
        FileOutputStream fos = null;
        try {
            String text = "";
            fos = new FileOutputStream(getExternalPath(),false);
            fos.write(text.getBytes());
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ex) {

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getExternalPath() {
        return (new File(Environment.getExternalStorageDirectory(), FILE_NAME));
    }

    // сохранение файла
    public void saveText(SensorEvent event) {

        if (!permissionGranted) {
            checkPermissions();
            return;
        }
        FileOutputStream fos = null;
        try {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            Date date = new Date();
            String printTime = sdf.format(date);
            String text = printTime + "x: " + Float.toString(x) +
                    "y: " + Float.toString(y) +
                    "z: " + Float.toString(z) + "\n";

            fos = new FileOutputStream(getExternalPath(),true);
            fos.write(text.getBytes());
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ex) {

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // открытие файла
    public void openText() {
        if (!permissionGranted) {
            checkPermissions();
            return;
        }

        FileInputStream fin = null;
        File file = getExternalPath();
        // если файл не существует, выход из метода
        if (!file.exists()) return;
        try {
            fin = new FileInputStream(file);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            recordResult.setText(text);
        } catch (IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {

            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // проверяем, доступно ли внешнее хранилище для чтения и записи
    public boolean isExternalStorageWriteable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // проверяем, доступно ли внешнее хранилище хотя бы только для чтения
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    private boolean checkPermissions() {

        if (!isExternalStorageReadable() || !isExternalStorageWriteable()) {
            Toast.makeText(this, "Внешнее хранилище не доступно", Toast.LENGTH_LONG).show();
            return false;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
            return false;
        } else {
            permissionGranted = true;
        }
        permissionGranted = true;
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}
