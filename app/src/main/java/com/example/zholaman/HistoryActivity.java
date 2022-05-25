package com.example.zholaman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private ListView listView;
    private static String url = "https://driver-behavior.herokuapp.com/historyResult";

    ArrayList<HashMap<String, String>> nameList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.listView);
        nameList = new ArrayList<>();
    }

    private class getNames extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            ListAdapter listAdapter = new SimpleAdapter(HistoryActivity.this, nameList, R.layout.item, new String[]{"acceleration_rate"}, new int[]{R.id.name});
            listView.setAdapter(listAdapter);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(HistoryActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Handler handler = new Handler();

            String jsonString = handler.httpServiceCall(url);
            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray dates = jsonObject.getJSONArray("Driver");
                    for (int i = 0; i <= dates.length(); i++) {
                        JSONObject jsonObject1 = dates.getJSONObject(i);
                        String acceleration_rate = jsonObject1.getString("acceleration_rate");
                        String braking_rate = jsonObject1.getString("braking_rate");
                        String cornering_rate = jsonObject1.getString("cornering_rate");

                        HashMap<String, String> dataMap = new HashMap<>();

                        dataMap.put("acceleration_rate", acceleration_rate);
                        dataMap.put("braking_rate", braking_rate);
                        dataMap.put("cornering_rate", cornering_rate);

                        nameList.add(dataMap);
                        System.out.println(nameList);

                    }
                } catch (JSONException e) {
                    Toast.makeText(HistoryActivity.this, "Json Parsing Error", Toast.LENGTH_SHORT).show();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json Parsing Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_LONG).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Json Parser error", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }

}
