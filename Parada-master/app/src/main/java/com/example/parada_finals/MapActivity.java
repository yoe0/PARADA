package com.example.parada_finals;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private BottomNavigationView bottomNavigation;
    private String username;
    private EditText etSearchMap;
    private List<Marker> allMarkers = new ArrayList<>();
    private MyLocationNewOverlay myLocationOverlay;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);

        username = getIntent().getStringExtra("USERNAME");

        // Header Username with Dropdown
        TextView tvUsernameHeader = findViewById(R.id.tvUsernameHeader);
        if (tvUsernameHeader != null) {
            if (username != null && !username.isEmpty()) {
                tvUsernameHeader.setText(username);
            }

            tvUsernameHeader.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(MapActivity.this, v);
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

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        GeoPoint zamboanga = new GeoPoint(6.9214, 122.0790);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(zamboanga);

        // Location overlay
        checkLocationPermissions();

        initializeMarkers();

        // Search Logic
        etSearchMap = findViewById(R.id.etSearchMap);
        etSearchMap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMarkers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bottom Navigation
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.nav_map);

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
                return true;
            }

            if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }
    }

    private void enableMyLocation() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeMarkers() {
        // Landmarks
        addMarker(6.9174, 122.0754, "KCC Mall de Zamboanga", "Gov. Camins Ave, Zamboanga City", "A popular shopping mall in Zamboanga City offering a wide variety of retail stores and dining options.");
        addMarker(6.9095, 122.0722, "ADZU", "La Purisima St, Zamboanga City", "Ateneo de Zamboanga University, a prestigious Jesuit educational institution.");
        addMarker(6.9400, 122.0488, "Pasonanca Park", "Pasonanca, Zamboanga City", "A scenic public park known for its lush greenery and cool climate.");
        addMarker(6.9004, 122.0825, "Fort Pilar Shrine", "NS Valderosa St, Zamboanga City", "A 17th-century Spanish military defense fortress and a major historical landmark.");
        addMarker(6.9248, 122.0594, "Zamboanga International Airport", "Moret IT, Zamboanga City", "The main gateway to Zamboanga City and the surrounding region.");
        addMarker(6.9090, 122.0750, "SM Mindpro", "La Purisima St, Zamboanga City", "One of the major shopping centers in the heart of Zamboanga City.");
        addMarker(6.9156, 122.0619, "WMSU", "Normal Rd, Zamboanga City", "Western Mindanao State University, a key public university in the region.");

        // Additional Barangays
        addMarker(6.8920, 122.1080, "Arena Blanco", "Brgy. Arena Blanco, Zamboanga City", "A coastal barangay known for its beautiful sea views and fishing community.");
        addMarker(6.9634, 121.9774, "Ayala", "Brgy. Ayala, Zamboanga City", "A bustling industrial and commercial center in the western coast of the city.");
        addMarker(6.9130, 122.0620, "Baliwasan", "Brgy. Baliwasan, Zamboanga City", "Home to various residential areas and key educational institutions.");
        addMarker(6.9530, 122.1330, "Boalan", "Brgy. Boalan, Zamboanga City", "A rapidly developing barangay with various commercial establishments.");
        addMarker(7.0850, 122.2220, "Bolong", "Brgy. Bolong, Zamboanga City", "Famous for its long stretch of white sand beaches and seafood.");
        addMarker(7.1550, 122.1850, "Bunguiao", "Brgy. Bunguiao, Zamboanga City", "An agricultural barangay known for its calm and green environment.");
        addMarker(7.0140, 122.1580, "Cabaluay", "Brgy. Cabaluay, Zamboanga City", "A coastal barangay with a growing residential and commercial presence.");
        addMarker(6.9360, 122.0640, "Cabatangan", "Brgy. Cabatgan, Zamboanga City", "Known for its elevated areas providing panoramic views of the city.");
        addMarker(6.9320, 122.0400, "Calarian", "Brgy. Calarian, Zamboanga City", "Location of the famous Zamboanga Golf Course and Southcom.");
        addMarker(6.9140, 122.0720, "Camino Nuevo", "Brgy. Camino Nuevo, Zamboanga City", "A centrally located barangay with many residential and small business areas.");
        addMarker(6.9060, 122.0600, "Campo Islam", "Brgy. Campo Islam, Zamboanga City", "A historic coastal barangay with a vibrant local culture.");
        addMarker(6.9150, 122.0680, "Canelar", "Brgy. Canelar, Zamboanga City", "Well-known for the Canelar Barter Trade Center.");
        addMarker(6.9650, 122.0420, "Capisan", "Brgy. Capisan, Zamboanga City", "A mountain barangay known for its cool air and fruit plantations.");
        addMarker(6.9680, 122.1450, "Culianan", "Brgy. Culianan, Zamboanga City", "A key agricultural and residential area in the eastern part of the city.");
        addMarker(7.2810, 122.2030, "Curuan", "Brgy. Curuan, Zamboanga City", "A large agricultural barangay and a hub for travelers heading north.");
        addMarker(6.9320, 122.0910, "Guiwan", "Brgy. Guiwan, Zamboanga City", "A major commercial and residential hub in the city.");
        addMarker(7.0370, 122.0120, "La Paz", "Brgy. La Paz, Zamboanga City", "Known for its scenic views and as a gateway to mountain trails.");
        addMarker(7.097982565963438, 121.9029452989571, "Labuan", "Brgy. Labuan, Zamboanga City", "A coastal barangay in the far west known for its fishing industry.");
        addMarker(7.143028800967661, 121.9028812175455, "Limpapa", "Brgy. Limpapa, Zamboanga City", "The westernmost barangay of Zamboanga City bordering Zamboanga del Norte.");
        addMarker(6.9640, 122.1070, "Lumbangan", "Brgy. Lumbangan, Zamboanga City", "A residential barangay with an active local community.");
        addMarker(6.9580, 122.0830, "Lunzuran", "Brgy. Lunzuran, Zamboanga City", "A residential area known for its peaceful surroundings.");
        addMarker(6.9580, 121.9960, "Maasin", "Brgy. Maasin, Zamboanga City", "Home to various natural attractions and a clean river.");
        addMarker(6.9200, 122.1150, "Mampang", "Brgy. Mampang, Zamboanga City", "A coastal barangay known for its salt beds and fishing.");
        addMarker(6.9825, 122.1353, "Mercedes", "Brgy. Mercedes, Zamboanga City", "A growing residential and commercial area along the highway.");
        addMarker(6.9940, 122.1510, "Pasobolong", "Brgy. Pasobolong, Zamboanga City", "An agricultural area known for its local produce.");
        addMarker(7.052846991950307, 121.9096976908118, "Patalon", "Brgy. Patalon, Zamboanga City", "A scenic coastal barangay in the western part of the city.");
        addMarker(6.9090, 122.0630, "San Jose Cawa-Cawa", "Brgy. San Jose Cawa-Cawa, Zamboanga City", "Home to the famous Cawa-Cawa Boulevard.");
        addMarker(6.9284, 122.0467, "San Jose Gusu", "Brgy. San Jose Gusu, Zamboanga City", "A busy residential and commercial barangay.");
        addMarker(7.0000, 121.9210, "San Ramon", "Brgy. San Ramon, Zamboanga City", "Known for the San Ramon Prison and Penal Farm.");
        addMarker(6.9250, 122.0710, "San Roque", "Brgy. San Roque, Zamboanga City", "A large residential barangay with several schools and businesses.");
        addMarker(7.0850, 122.1930, "Sangali", "Brgy. Sangali, Zamboanga City", "Known for its major fishing port and seafood markets.");
        addMarker(6.9080, 122.0790, "Santa Barbara", "Brgy. Santa Barbara, Zamboanga City", "A historic barangay in the city proper.");
        addMarker(6.9050, 122.0860, "Santa Catalina", "Brgy. Santa Catalina, Zamboanga City", "A vibrant residential and commercial area near the coast.");
        addMarker(6.9310, 122.0740, "Santa Maria", "Brgy. Santa Maria, Zamboanga City", "One of the city's largest residential and commercial barangays.");
        addMarker(6.9140, 122.0580, "Santo Niño", "Brgy. Santo Niño, Zamboanga City", "A residential barangay with a strong community feel.");
        addMarker(6.9402, 122.0104, "Sinunoc", "Brgy. Sinunoc, Zamboanga City", "A scenic coastal barangay in the western side.");
        addMarker(7.022851009058555, 121.9192716473782, "Sinubong", "Brgy. Sinubong, Zamboanga City", "a local area or community known as a common pickup and drop-off point for commuters..");
        // Updated Talisayan coordinates from Google Maps iframe data
        addMarker(6.9876, 121.9296, "Talisayan", "Brgy. Talisayan, Zamboanga City", "Known for its beaches and as a key transport hub for the western part of the city.");
        addMarker(6.9110, 122.1020, "Talon-Talon", "Brgy. Talon-Talon, Zamboanga City", "A coastal barangay known for its seafood and vibrant community.");
        addMarker(6.9183, 122.0838, "Tetuan", "Brgy. Tetuan, Zamboanga City", "A major residential and commercial center, often called a city within a city.");
        addMarker(6.9160, 122.0960, "Tugbungan", "Brgy. Tugbungan, Zamboanga City", "A residential barangay with various local businesses.");
        addMarker(7.3780, 122.2850, "Vitali", "Brgy. Vitali, Zamboanga City", "The city's northernmost commercial hub and agricultural center.");
        addMarker(6.9550, 122.1240, "Zambowood", "Brgy. Zambowood, Zamboanga City", "A peaceful residential community located on the hills.");
    }

    private void addMarker(double lat, double lon, String title, String address, String description) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setTitle(title);
        marker.setSnippet(address + "|" + description); // Store both using a separator
        
        marker.setOnMarkerClickListener((m, mapView) -> {
            GeoPoint markerPos = m.getPosition();
            String distanceText = "Calculating...";

            if (myLocationOverlay != null && myLocationOverlay.getMyLocation() != null) {
                GeoPoint userPos = myLocationOverlay.getMyLocation();
                float[] results = new float[1];
                Location.distanceBetween(userPos.getLatitude(), userPos.getLongitude(),
                        markerPos.getLatitude(), markerPos.getLongitude(), results);
                
                float distanceInMeters = results[0];
                if (distanceInMeters < 1000) {
                    distanceText = String.format("%.0f m away", distanceInMeters);
                } else {
                    distanceText = String.format("%.2f km away", distanceInMeters / 1000.0);
                }
            } else {
                distanceText = "Enable GPS to see distance";
            }

            String[] parts = m.getSnippet().split("\\|");
            String addr = parts[0];
            String desc = parts.length > 1 ? parts[1] : "";

            showPlaceDetails(m.getTitle(), addr, desc, distanceText);
            return true;
        });

        mapView.getOverlays().add(marker);
        allMarkers.add(marker);
    }

    private void showPlaceDetails(String title, String address, String description, String distance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_place_details, null);
        
        TextView tvTitle = view.findViewById(R.id.tvPlaceTitle);
        TextView tvAddress = view.findViewById(R.id.tvPlaceAddress);
        Button btnClose = view.findViewById(R.id.btnCloseModal);
        
        tvTitle.setText(title);
        // Combine address, description and distance
        String combinedInfo = address + "\n\n" + description + "\n\n📍 " + distance;
        tvAddress.setText(combinedInfo);
        
        builder.setView(view);
        AlertDialog dialog = builder.create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        
        btnClose.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private void filterMarkers(String query) {
        String lowerQuery = query.toLowerCase();
        mapView.getOverlays().clear();
        
        if (myLocationOverlay != null) {
            mapView.getOverlays().add(myLocationOverlay);
        }
        
        for (Marker marker : allMarkers) {
            if (marker.getTitle().toLowerCase().contains(lowerQuery)) {
                mapView.getOverlays().add(marker);
            }
        }
        
        if (query.isEmpty()) {
            mapView.getController().animateTo(new GeoPoint(6.9214, 122.0790));
        }
        
        mapView.invalidate();
    }

    private void logout() {
        Intent intent = new Intent(MapActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
    }
}
