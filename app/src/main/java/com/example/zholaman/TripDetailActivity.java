package com.example.zholaman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TripDetailActivity extends AppCompatActivity {

    TextView safetyscore,distance,aggresive,speed,breaking,datatime;
    ImageView back;
    private GoogleMap mMap;
    Location myLocation = null;
    Location destinationLocation = null;
    int user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        datatime = findViewById(R.id.textView4);
        safetyscore = findViewById(R.id.textView34);
        distance = findViewById(R.id.textView16);
        aggresive = findViewById(R.id.textView18);
        speed = findViewById(R.id.textView20);
        breaking = findViewById(R.id.textView22);
        back = findViewById(R.id.imageView6);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String passedUsername = intent.getStringExtra("data");
            user_id = intent.getIntExtra("user_id", 2);
        }
        System.out.println("ID: " + user_id);

        bottomNavigationView.setSelectedItemId(R.id.history);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.history:
                        startActivity(new Intent(getApplicationContext(),HistoryActivity.class).putExtra("user_id", user_id));
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HistoryActivity.class).putExtra("user_id", user_id));
                overridePendingTransition(0,0);
            }
        });

    }
}