package com.example.zholaman;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManagers;
    private SensorManager sensorManagerG;
    private Sensor senAccelerometor;
    private Sensor senGyroscope;
    private TextView GPSx;
    private TextView GPSy;
    private TextView GPS_loc;
    private FusedLocationProviderClient MyFusedLocationClient;

    private long lastUpdate = 0;
    private long lastUpdate_gyro = 0;
    private int locationRequestCode = 1000;
    private double wayLatitude, wayLongitude;
    public float x, y, z, gx, gy, gz;
    public int i = 0;
    public int c = 0;
    public int user = 0;
    public String fileString = "";
    public String name = "";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    String currentDateandTime = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        return true;
                    case R.id.map:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


        MyFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sensorManagers = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManagerG = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManagers != null;
        senAccelerometor = sensorManagers.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senGyroscope = sensorManagerG.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, locationRequestCode);
        } else {
//            Toast T = Toast.makeText(getApplicationContext(), "Location & file access Permission Granted", Toast.LENGTH_SHORT);
//            T.show();
        }


        Switch toggle = (Switch) findViewById(R.id.sw);
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                i = 1;
                onResume();
            } else {
                i = 0;
                onPause();
            }
        });
    }

    public void DatabaseWriter(String driving_name, float ax, float ay, float az, double g1, double g2, float gx, float gy, float gz, String time, int user_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://driver-behavior.herokuapp.com/saveData";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("driving_name", driving_name);
                params.put("AccX", String.valueOf(ax));
                params.put("AccY", String.valueOf(ay));
                params.put("AccZ", String.valueOf(az));
                params.put("GPS_Long", String.valueOf(g1));
                params.put("GPS_Lat", String.valueOf(g2));
                params.put("GyroX", String.valueOf(gx));
                params.put("GyroY", String.valueOf(gy));
                params.put("GyroZ", String.valueOf(gz));
                params.put("TimeStamp", time);
                params.put("user_id", String.valueOf(user_id));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void GetEndResultOfDriving() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = String.format("https://driver-behavior.herokuapp.com/GetDriverLastData/%s/%s", user, name);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(stringRequest);
    }

    protected void onPause() {
        super.onPause();
        sensorManagers.unregisterListener(this);
        GetEndResultOfDriving();
    }

    protected void onResume() {
        super.onResume();
        if (i == 1) {
            sensorManagers.registerListener(this, senAccelerometor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManagers.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    public void GetNewLocation() {
        MyFusedLocationClient.flushLocations();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        MyFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                wayLatitude = location.getLatitude();
                wayLongitude = location.getLongitude();

                GPSx = (TextView) findViewById(R.id.gpsx);
                GPSx.setText("" + wayLatitude);

                GPSy = (TextView) findViewById(R.id.gpsy);
                GPSy.setText("" + wayLongitude);

                fileString = fileString + wayLatitude + ", " + wayLongitude + ", ";

                GPS_loc = (TextView) findViewById(R.id.city);
                GPS_loc.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;
        Sensor GSensor = sensorEvent.sensor;


        if (GSensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx = sensorEvent.values[0];
            gy = sensorEvent.values[1];
            gz = sensorEvent.values[2];

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastUpdate_gyro > 300)) {

                lastUpdate_gyro = currentTime;

                {
                    GetNewLocation();                                   //Just to update the location ;P


                    String sX = Float.toString(gx);
                    TextView text = (TextView) findViewById(R.id.gx);
                    text.setText(sX);

                    String sY = Float.toString(gy);
                    text = (TextView) findViewById(R.id.gy);
                    text.setText(sY);

                    String sZ = Float.toString(gz);
                    text = (TextView) findViewById(R.id.gz);
                    text.setText(sZ);

                    fileString = fileString + sX + ", " + sY + ", " + sZ + ", ";
                }
            }
        }

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastUpdate) > 300) {
                long timeDiff = currentTime - lastUpdate;
                lastUpdate = currentTime;

                {
                    String sX = Float.toString(x);
                    TextView text = (TextView) findViewById(R.id.ax);
                    text.setText(sX);

                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    int progress = (int) (((-1 * x) + 10) * 10000);
                    progressBar.setProgress(progress);

                    String sY = Float.toString(y);
                    text = (TextView) findViewById(R.id.ay);
                    text.setText(sY);

                    String sZ = Float.toString(z);
                    text = (TextView) findViewById(R.id.az);
                    text.setText(sZ);

                    fileString = fileString + sX + ", " + sY + ", " + sZ + "\n";
//                    FileWriters(fileString);
                    Intent intent = getIntent();
                    if (intent.getExtras() != null) {
                        user = intent.getIntExtra("user_id", 2);
                        System.out.println("User is this " + user);
                    } else {
                        user = 1;
                    }
                    name = String.valueOf(user) + currentDateandTime;
                    Date date = new Date(lastUpdate);
                    Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String ll_update_time = format.format(date);
                    DatabaseWriter(name, x, y, z, wayLongitude, wayLatitude, gx, gy, gz, ll_update_time, user);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}