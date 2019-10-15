package com.example.paytapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    TextView tv_fullTransaction;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // --------------------- Bottom Nanigation ---------------------------

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent intent;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.bottom_navigation_Dashboard_nfc:
                        Toast.makeText(Dashboard.this, "NfC", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_navigation_Dashboard_scan:
                        Toast.makeText(Dashboard.this, "Scan", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.bottom_navigation_Dashboard_bank:
                        Toast.makeText(Dashboard.this, "Bank", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

    }
}
