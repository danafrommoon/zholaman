package com.example.zholaman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {
    ListView sensorResultList;
    ProgressDialog progressDialog;
    TextView name;
    ArrayList<HashMap<String, String>> sensorsResultList;
    CardView toTripDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        name = findViewById(R.id.bigwelcome_text);
        toTripDetail = findViewById(R.id.cardViewtotrip);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.history);
        int user_id = 0;
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            user_id = intent.getIntExtra("user_id", 2);
            System.out.println("Username ID: " + user_id);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.history:
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
        String url = String.format("https://driver-behavior.herokuapp.com/history/%s", user_id);
        sensorResultList = findViewById(R.id.sensorList);
        sensorsResultList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseJsonData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(HistoryActivity.this);
        requestQueue.add(request);
    }

    public void parseJsonData(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            JSONArray sensorArray = object.getJSONArray("User_result");

            for (int i = 0; i < sensorArray.length(); i++) {
                JSONObject results = sensorArray.getJSONObject(i);
                String arr_rate = results.getString("acceleration_rate");
                String braking_rate = results.getString("braking_rate");
                String cornering_rate = results.getString("cornering_rate");
                String t_start = results.getString("timestamp_start");
                String score = results.getString("safety_score");

                HashMap<String, String> sensor = new HashMap<>();

                sensor.put("acceleration_rate", arr_rate);
                sensor.put("braking_rate", braking_rate);
                sensor.put("cornering_rate", cornering_rate);
                sensor.put("timestamp_start", t_start);
                sensor.put("safety_score", score);
                sensorsResultList.add(sensor);
            }
            ListAdapter adapter = new SimpleAdapter(HistoryActivity.this, sensorsResultList,
                    R.layout.activity_list,
                    new String[]{"acceleration_rate", "braking_rate", "cornering_rate", "timestamp_start", "safety_score"},
                    new int[]{R.id.breakingnumber, R.id.aggresivepercent, R.id.speednumber, R.id.data, R.id.safety_score2});
            sensorResultList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }


}
