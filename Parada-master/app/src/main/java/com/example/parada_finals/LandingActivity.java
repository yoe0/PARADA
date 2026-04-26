package com.example.parada_finals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        String username = getIntent().getStringExtra("USERNAME");

        // Display Username in Card
        TextView tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        if (username != null && !username.isEmpty()) {
            tvWelcomeUser.setText("Buenas Dias, " + username + "! 👋");
        }

        // Header Username with Dropdown
        TextView tvUsernameHeader = findViewById(R.id.tvUsernameHeader);
        if (username != null && !username.isEmpty()) {
            tvUsernameHeader.setText(username);
        }

        tvUsernameHeader.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(LandingActivity.this, v);
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

        setupMenuCards();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_how_to);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_map) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                return true;
            }

            if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                return true;
            }

            if (id == R.id.nav_routes) {
                Intent intent = new Intent(this, RoutesActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                return true;
            }

            return id == R.id.nav_how_to;
        });
    }

    private void setupMenuCards() {
        // Search Destination Card
        View cardSearch = findViewById(R.id.cardSearch);
        updateCard(cardSearch, 
            "Search your Destination", 
            "Use the Map page to search for your destination. Enter your starting point and where you want to go.",
            android.R.drawable.ic_menu_search,
            R.color.card_red_side);
        cardSearch.setOnClickListener(v -> navigateTo(MapActivity.class));

        // View Routes Card
        View cardRoutes = findViewById(R.id.cardRoutes);
        updateCard(cardRoutes, 
            "View Routes/ Check Schedules", 
            "Browse the Routes page to find the bus or train line that serves your area, then view real-time arrival times and plan your trip with schedules updated live for the most accurate information.",
            android.R.drawable.ic_menu_directions,
            R.color.card_purple_side);
        cardRoutes.setOnClickListener(v -> navigateTo(RoutesActivity.class));

        // Tickets Card
        View cardTickets = findViewById(R.id.cardTickets);
        updateCard(cardTickets, 
            "Purchase Tickets/Notifications", 
            "Check fare prices and explore flexible payment options, including single rides, day passes, and monthly passes. You can also enable notifications to receive service alerts, delays, and updates about your favorite routes.",
            android.R.drawable.ic_menu_agenda,
            R.color.card_green_side);

        // Service Alerts Card
        View cardAlerts = findViewById(R.id.cardAlerts);
        updateCard(cardAlerts, 
            "Service Alerts", 
            "Stay informed about disruptions, maintenance, and other important service updates.",
            android.R.drawable.ic_popup_reminder,
            R.color.card_orange_side);
    }

    private void updateCard(View card, String title, String desc, int iconRes, int colorRes) {
        TextView tvTitle = card.findViewById(R.id.itemTitle);
        TextView tvDesc = card.findViewById(R.id.itemDesc);
        ImageView ivIcon = card.findViewById(R.id.itemIcon);
        View sideBorder = card.findViewById(R.id.sideBorder);

        tvTitle.setText(title);
        tvDesc.setText(desc);
        ivIcon.setImageResource(iconRes);
        
        int color = ContextCompat.getColor(this, colorRes);
        sideBorder.setBackgroundColor(color);
        ivIcon.setColorFilter(color);
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME"));
        startActivity(intent);
    }

    private void logout() {
        Intent intent = new Intent(LandingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}