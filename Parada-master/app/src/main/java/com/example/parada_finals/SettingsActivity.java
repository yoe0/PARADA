package com.example.parada_finals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String username = getIntent().getStringExtra("USERNAME");

        // Header Username with Dropdown
        TextView tvUsernameHeader = findViewById(R.id.tvUsernameHeader);
        if (tvUsernameHeader != null) {
            if (username != null && !username.isEmpty()) {
                tvUsernameHeader.setText(username);
            }

            tvUsernameHeader.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(SettingsActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.menu_logout) {
                        logout();
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

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

        // Updated click listeners to use CardView IDs from the new layout
        setupClick(R.id.account, AccountActivity.class);
        setupClick(R.id.location, LocationActivity.class);
        setupClick(R.id.privacy, PrivacyActivity.class);
        setupClick(R.id.about, AboutActivity.class);
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