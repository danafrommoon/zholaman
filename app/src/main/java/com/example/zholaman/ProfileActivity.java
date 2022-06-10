package com.example.zholaman;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    TextView name;
    int user_id;
    Button logout;
    TextView tt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        name = findViewById(R.id.bigwelcome_text);
        logout = findViewById(R.id.button);
        tt = findViewById(R.id.driver_profiles);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String passedUsername = intent.getStringExtra("data");
            user_id = intent.getIntExtra("user_id", 2);
            name.setText(passedUsername);
        }


        System.out.println("ID: " + user_id);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class).putExtra("user_id", user_id));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.history:
                        startActivity(new Intent(getApplicationContext(), HistoryActivity.class).putExtra("user_id", user_id));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TripDetailActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }
}