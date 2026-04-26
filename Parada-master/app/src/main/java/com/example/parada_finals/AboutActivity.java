package com.example.parada_finals;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String username = getIntent().getStringExtra("USERNAME");

        // Header Username with Dropdown
        TextView tvUsernameHeader = findViewById(R.id.tvUsernameHeader);
        if (tvUsernameHeader != null) {
            if (username != null && !username.isEmpty()) {
                tvUsernameHeader.setText(username);
            }

            tvUsernameHeader.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(AboutActivity.this, v);
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

        // Bottom Navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
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

            if (id == R.id.nav_settings) {
                finish(); // Go back to Settings
                return true;
            }

            return false;
        });
    }

    private void logout() {
        Intent intent = new Intent(AboutActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}