package com.example.parada_finals;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RoutesActivity extends AppCompatActivity {

    private Spinner spinnerVehicleType, spinnerFrom, spinnerTo;
    private Button btnCalculate;
    private CardView cardResult;
    private TextView tvResultDistance, tvResultFare;
    private BottomNavigationView bottomNavigationView;

    private String[] vehicleTypes = {"Tricycle", "Jeepney"};
    private String[] barangayList = {
            "Town", "Arena Blanco", "Ayala", "Baliwasan", "Baluno", "Boalan", "Bolong", "Buenavista", "Bunguiao",
            "Busay (Sacol Island)", "Cabaluay", "Cabatangan", "Cacao", "Calabasa", "Calarian", "Camino Nuevo",
            "Campo Islam", "Canelar", "Capisan", "Cawit", "Culianan", "Curuan", "Dita", "Divisoria",
            "Dulian (Upper Bunguiao)", "Dulian (Upper Pasonanca)", "Guisao", "Guiwan", "Kasanyangan", "La Paz",
            "Labuan", "Lamisahan", "Landang Gua", "Landang Laum", "Lanzones", "Lapakan", "Latuan (Curuan)",
            "Licomo", "Limaong", "Limpapa", "Lubigan", "Lumayang", "Lumbangan", "Lunzuran", "Maasin",
            "Malagutay", "Mampang", "Manalipa", "Mangusu", "Manicahan", "Mariki", "Mercedes", "Muti",
            "Pamucutan", "Pangapuyan", "Panubigan", "Pasilmanta (Sacol Island)", "Pasobolong", "Pasonanca",
            "Patalon", "Putik", "Quiniput", "Recodo", "Rio Hondo", "Salaan", "San Jose Cawa-Cawa",
            "San Jose Gusu", "San Ramon", "San Roque", "Sangali", "Santa Barbara", "Santa Catalina",
            "Santa Maria", "Santo Niño", "Sibulao (Caruan)", "Sinubung", "Sinunoc", "Tagasilay", "Taguiti",
            "Talabaan", "Talisayan", "Talon-Talon", "Taluksangay", "Tetuan", "Tictapul", "Tigbalabag",
            "Tigtabon", "Tolosa", "Tugbungan", "Tulungatung", "Tumaga", "Tumalutab", "Tumitus", "Victoria",
            "Vitali", "Zambowood", "Zone I (Poblacion)", "Zone II (Poblacion)", "Zone III (Poblacion)",
            "Zone IV (Poblacion)"
    };

    private Map<String, LatLng> coordinatesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        initializeCoordinates();
        String username = getIntent().getStringExtra("USERNAME");

        // Header
        TextView tvUsernameHeader = findViewById(R.id.tvUsernameHeader);
        if (tvUsernameHeader != null) {
            if (username != null && !username.isEmpty()) {
                tvUsernameHeader.setText(username);
            }
            tvUsernameHeader.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(this, v);
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

        // Initialize UI
        spinnerVehicleType = findViewById(R.id.spinnerVehicleType);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        btnCalculate = findViewById(R.id.btnCalculate);
        cardResult = findViewById(R.id.cardResult);
        tvResultDistance = findViewById(R.id.tvResultDistance);
        tvResultFare = findViewById(R.id.tvResultFare);

        // Setup Spinners
        ArrayAdapter<String> vehicleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vehicleTypes);
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleType.setAdapter(vehicleAdapter);

        ArrayAdapter<String> locAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, barangayList);
        locAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(locAdapter);
        spinnerTo.setAdapter(locAdapter);

        btnCalculate.setOnClickListener(v -> calculateFare());

        // Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_routes);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_how_to) {
                Intent intent = new Intent(this, LandingActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_map) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
                return true;
            }
            return id == R.id.nav_routes;
        });
    }

    private void initializeCoordinates() {
        // Sample coordinates for key Zamboanga locations
        coordinatesMap.put("Town", new LatLng(6.905, 122.075));
        coordinatesMap.put("KCC Mall", new LatLng(6.9174, 122.0754));
        coordinatesMap.put("SM Mindpro", new LatLng(6.9067, 122.0772));
        coordinatesMap.put("Tetuan", new LatLng(6.9125, 122.0833));
        coordinatesMap.put("Santa Maria", new LatLng(6.9244, 122.0644));
        coordinatesMap.put("Putik", new LatLng(6.9388, 122.0888));
        coordinatesMap.put("Pasonanca", new LatLng(6.9400, 122.0488));
        coordinatesMap.put("Manicahan", new LatLng(6.9833, 122.1833));
        coordinatesMap.put("Sangali", new LatLng(7.0167, 122.2167));
        coordinatesMap.put("Ayala", new LatLng(6.9500, 121.9667));
        coordinatesMap.put("Guiwan", new LatLng(6.9231, 122.0911));
        coordinatesMap.put("San Roque", new LatLng(6.9101, 122.0555));
        coordinatesMap.put("Talon-Talon", new LatLng(6.9083, 122.1000));
        coordinatesMap.put("Tumaga", new LatLng(6.9333, 122.0667));
    }

    private LatLng getLatLng(String name) {
        if (coordinatesMap.containsKey(name)) {
            return coordinatesMap.get(name);
        }
        Random r = new Random(name.hashCode());
        double lat = 6.90 + (r.nextDouble() * 0.15);
        double lng = 122.05 + (r.nextDouble() * 0.15);
        return new LatLng(lat, lng);
    }

    private void calculateFare() {
        if (spinnerFrom.getSelectedItem() == null || spinnerTo.getSelectedItem() == null) {
            Toast.makeText(this, "Please select locations", Toast.LENGTH_SHORT).show();
            return;
        }

        String from = spinnerFrom.getSelectedItem().toString();
        String to = spinnerTo.getSelectedItem().toString();
        String vehicle = spinnerVehicleType.getSelectedItem().toString();

        if (from.equals(to)) {
            Toast.makeText(this, "From and To cannot be the same", Toast.LENGTH_SHORT).show();
            cardResult.setVisibility(View.GONE);
            return;
        }

        LatLng start = getLatLng(from);
        LatLng end = getLatLng(to);

        float[] results = new float[1];
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results);
        
        double roadDistanceKm = (results[0] / 1000.0) * 1.3;

        double fare = 0;

        if (vehicle.equals("Tricycle")) {
            fare = roadDistanceKm * 15;
            tvResultDistance.setText(String.format("Estimated Distance: %.2f km (%d m)", roadDistanceKm, (int)(roadDistanceKm * 1000)));
        } else {
            fare = 13.0;
            if (roadDistanceKm > 4) {
                fare += (roadDistanceKm - 4) * 2.0;
            }
            tvResultDistance.setText("Route: " + from + " to " + to + String.format(" (%.2f km)", roadDistanceKm));
        }

        tvResultFare.setText(String.format("Fare: ₱%.2f", fare));
        cardResult.setVisibility(View.VISIBLE);
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
