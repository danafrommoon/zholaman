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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        name = findViewById(R.id.bigwelcome_text);
        int user_id = 0;
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            user_id = intent.getIntExtra("user_id", 2);
            System.out.println("Username ID: " + user_id);
        }
        String url = String.format("https://driver-behavior.herokuapp.com/history/%s", user_id);
        sensorResultList = findViewById(R.id.sensorList);
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
            ArrayList arrayList = new ArrayList();
            ArrayList acc_Rate = new ArrayList();

            for (int i = 0; i < sensorArray.length(); i++) {
                JSONObject results = sensorArray.getJSONObject(i);

                String rating = "Start time: " + results.getString("timestamp_start") + " " + "End time: " + results.getString("timestamp_end") +
                        "Acceleration rate: " + results.getString("acceleration_rate")
                        + "\n" + "Braking rate: " + results.getString("braking_rate") +
                        "\n" + "Cornering rate: " + results.getString("cornering_rate");
                String arr_rate = results.getString("acceleration_rate");
                acc_Rate.add(arr_rate);
                arrayList.add(rating);
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, arrayList);
            sensorResultList.setAdapter(arrayAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }
}
