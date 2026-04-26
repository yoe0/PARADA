package com.example.parada_finals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.nav_settings);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_how_to) {
                startActivity(new Intent(this, LandingActivity.class));
                finish();
                return true;
            }

            if (id == R.id.nav_routes) {
                startActivity(new Intent(this, RoutesActivity.class));
                finish();
                return true;
            }

            if (id == R.id.nav_map) {
                startActivity(new Intent(this, MapActivity.class));
                finish();
                return true;
            }

            return id == R.id.nav_settings;
        });

        Switch notifSwitch = findViewById(R.id.switchNotifications);

        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Notifications ON
            } else {
                // Notifications OFF
            }
        });

        LinearLayout account = findViewById(R.id.account);

        account.setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
        });

        setupClick(R.id.location, LocationActivity.class);
        setupClick(R.id.privacy, PrivacyActivity.class);
        setupClick(R.id.about, AboutActivity.class);

        // ✅ LOGOUT FUNCTION
        findViewById(R.id.logout).setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);

            // clears all previous activities (prevents back button return)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }

    private void setupClick(int viewId, Class<?> activityClass) {
        View v = findViewById(viewId);
        if (v != null) {
            v.setOnClickListener(view ->
                    startActivity(new Intent(this, activityClass))
            );
        }
    }
}