package com.example.parada_finals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String username = getIntent().getStringExtra("USERNAME");

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.nav_settings);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_how_to) {
                Intent intent = new Intent(this, LandingActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
                return true;
            }
            if (id == R.id.nav_routes) {
                Intent intent = new Intent(this, RoutesActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
                return true;
            }
            if (id == R.id.nav_map) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
                return true;
            }
            return id == R.id.nav_settings;
        });

        setupClick(R.id.account, AccountActivity.class);
        setupClick(R.id.location, LocationActivity.class);
        setupClick(R.id.privacy, PrivacyActivity.class);
        setupClick(R.id.about, AboutActivity.class);

        // Explicit Logout Button logic
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }

    private void setupClick(int viewId, Class<?> activityClass) {
        View v = findViewById(viewId);
        if (v != null) {
            v.setOnClickListener(view -> {
                Intent intent = new Intent(this, activityClass);
                intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME"));
                startActivity(intent);
            });
        }
    }

    private void logout() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}