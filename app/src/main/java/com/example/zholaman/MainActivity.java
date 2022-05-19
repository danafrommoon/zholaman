package com.example.zholaman;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //adding gyroscope and accelerometer
    TextView AccX, AccY,AccZ, GyroX, GyroY, GyroZ;
    SensorManager sensorMan;
    private Sensor accelerometer, mGyro;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.map:
                        startActivity(new Intent(getApplicationContext(),MapActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null){
            sensorMan.registerListener(MainActivity.this, accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered accelerometer listener");
        }else{
            AccX.setText("Accelerometer Not Supported");
            AccY.setText("Accelerometer Not Supported");
            AccZ.setText("Accelerometer Not Supported");
        }

        mGyro = sensorMan.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mGyro != null){
            sensorMan.registerListener(MainActivity.this, mGyro,SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered gyroscope listener");
        }else{
            GyroX.setText("Gyroscope Not Supported");
            GyroY.setText("Gyroscope Not Supported");
            GyroZ.setText("Gyroscope Not Supported");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: X:"+ event.values[0] + "Y:" + event.values[1] + "Z:" + event.values[2]);
        AccX.setText("AccX: "+event.values[0]);
        AccY.setText("AccY: "+event.values[1]);
        AccZ.setText("AccZ: "+event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}