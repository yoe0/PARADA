package com.example.parada_finals;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class RoutesActivity extends AppCompatActivity {

    private EditText etOrigin, etDestination;
    private Button btnCalculate;
    private CardView cardResult;
    private TextView tvResultDistance, tvResultFare;
    private BottomNavigationView bottomNavigationView;
    
    private LinearLayout btnTricycle, btnJeepney;
    private String selectedVehicle = "Tricycle"; // Default

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
        String destinationFromMap = getIntent().getStringExtra("DESTINATION");

        // Initialize UI
        etOrigin = findViewById(R.id.etOrigin);
        etDestination = findViewById(R.id.etDestination);
        btnTricycle = findViewById(R.id.btnTricycle);
        btnJeepney = findViewById(R.id.btnJeepney);
        btnCalculate = findViewById(R.id.btnCalculate);
        cardResult = findViewById(R.id.cardResult);
        tvResultDistance = findViewById(R.id.tvResultDistance);
        tvResultFare = findViewById(R.id.tvResultFare);

        // Setup Location Pickers
        etOrigin.setOnClickListener(v -> showLocationPickerDialog(etOrigin));
        etDestination.setOnClickListener(v -> showLocationPickerDialog(etDestination));

        // Setup Selection Logic
        btnTricycle.setOnClickListener(v -> selectVehicle("Tricycle"));
        btnJeepney.setOnClickListener(v -> selectVehicle("Jeepney"));

        // Set destination if passed from Map
        if (destinationFromMap != null) {
            etDestination.setText(destinationFromMap);
        }

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

    private void showLocationPickerDialog(EditText targetField) {
        List<String> names = new ArrayList<>(Arrays.asList(barangayList));
        Collections.sort(names);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_location_picker, null);
        AlertDialog dialog = new AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        RecyclerView rvLocations = dialogView.findViewById(R.id.rvLocations);
        EditText etSearch = dialogView.findViewById(R.id.etSearchLocation);
        View btnClose = dialogView.findViewById(R.id.btnClosePicker);

        rvLocations.setLayoutManager(new LinearLayoutManager(this));
        
        // Using a simpler adapter for barangay list or reuse LocationAdapter if possible
        // For simplicity, let's create a local adapter logic or similar
        LocationAdapter adapter = new LocationAdapter(names, locationName -> {
            targetField.setText(locationName);
            dialog.dismiss();
        });
        rvLocations.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase();
                List<String> filtered = names.stream()
                        .filter(n -> n.toLowerCase().contains(query))
                        .collect(Collectors.toList());
                adapter.updateList(filtered);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }
        dialog.show();
    }

    private void selectVehicle(String type) {
        selectedVehicle = type;
        int orange = ContextCompat.getColor(this, R.color.ui_orange);
        int dark = ContextCompat.getColor(this, R.color.ui_input_bg);
        int white = Color.WHITE;
        int grey = ContextCompat.getColor(this, R.color.grey_600);

        if (type.equals("Tricycle")) {
            btnTricycle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(orange));
            ((TextView) btnTricycle.getChildAt(1)).setTextColor(white);
            ((android.widget.ImageView) btnTricycle.getChildAt(0)).setColorFilter(white);

            btnJeepney.setBackgroundTintList(android.content.res.ColorStateList.valueOf(dark));
            ((TextView) btnJeepney.getChildAt(1)).setTextColor(grey);
            ((android.widget.ImageView) btnJeepney.getChildAt(0)).setColorFilter(grey);
        } else {
            btnJeepney.setBackgroundTintList(android.content.res.ColorStateList.valueOf(orange));
            ((TextView) btnJeepney.getChildAt(1)).setTextColor(white);
            ((android.widget.ImageView) btnJeepney.getChildAt(0)).setColorFilter(white);

            btnTricycle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(dark));
            ((TextView) btnTricycle.getChildAt(1)).setTextColor(grey);
            ((android.widget.ImageView) btnTricycle.getChildAt(0)).setColorFilter(grey);
        }
    }

    private void initializeCoordinates() {
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
        coordinatesMap.put("Arena Blanco", new LatLng(6.8920, 122.1080));
        coordinatesMap.put("Baliwasan", new LatLng(6.9130, 122.0620));
        coordinatesMap.put("Boalan", new LatLng(6.9530, 122.1330));
        coordinatesMap.put("Bolong", new LatLng(7.0850, 122.2220));
        coordinatesMap.put("Bunguiao", new LatLng(7.1550, 122.1850));
        coordinatesMap.put("Cabaluay", new LatLng(7.0140, 122.1580));
        coordinatesMap.put("Cabatangan", new LatLng(6.9360, 122.0640));
        coordinatesMap.put("Calarian", new LatLng(6.9320, 122.0400));
        coordinatesMap.put("Camino Nuevo", new LatLng(6.9140, 122.0720));
        coordinatesMap.put("Campo Islam", new LatLng(6.9060, 122.0600));
        coordinatesMap.put("Canelar", new LatLng(6.9150, 122.0680));
        coordinatesMap.put("Capisan", new LatLng(6.9650, 122.0420));
        coordinatesMap.put("Culianan", new LatLng(6.9680, 122.1450));
        coordinatesMap.put("Curuan", new LatLng(7.2810, 122.2030));
        coordinatesMap.put("La Paz", new LatLng(7.0370, 122.0120));
        coordinatesMap.put("Labuan", new LatLng(7.0979, 121.9029));
        coordinatesMap.put("Limpapa", new LatLng(7.1430, 121.9028));
        coordinatesMap.put("Lumbangan", new LatLng(6.9640, 122.1070));
        coordinatesMap.put("Lunzuran", new LatLng(6.9580, 122.0830));
        coordinatesMap.put("Maasin", new LatLng(6.9580, 121.9960));
        coordinatesMap.put("Mampang", new LatLng(6.9200, 122.1150));
        coordinatesMap.put("Mercedes", new LatLng(6.9825, 122.1353));
        coordinatesMap.put("Pasobolong", new LatLng(6.9940, 122.1510));
        coordinatesMap.put("Patalon", new LatLng(7.0528, 121.9096));
        coordinatesMap.put("San Jose Cawa-Cawa", new LatLng(6.9090, 122.0630));
        coordinatesMap.put("San Jose Gusu", new LatLng(6.9284, 122.0467));
        coordinatesMap.put("San Ramon", new LatLng(7.0000, 121.9210));
        coordinatesMap.put("Santa Barbara", new LatLng(6.9080, 122.0790));
        coordinatesMap.put("Santa Catalina", new LatLng(6.9050, 122.0860));
        coordinatesMap.put("Santo Niño", new LatLng(6.9140, 122.0580));
        coordinatesMap.put("Sinunoc", new LatLng(6.9402, 122.0104));
        coordinatesMap.put("Sinubung", new LatLng(7.0228, 121.9192));
        coordinatesMap.put("Talisayan", new LatLng(6.9876, 121.9296));
        coordinatesMap.put("Tugbungan", new LatLng(6.9160, 122.0960));
        coordinatesMap.put("Vitali", new LatLng(7.3780, 122.2850));
        coordinatesMap.put("Zambowood", new LatLng(6.9550, 122.1240));
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
        String from = etOrigin.getText().toString();
        String to = etDestination.getText().toString();

        if (from.isEmpty() || to.isEmpty()) {
            Toast.makeText(this, "Please select locations", Toast.LENGTH_SHORT).show();
            return;
        }

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

        if (selectedVehicle.equals("Tricycle")) {
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
}