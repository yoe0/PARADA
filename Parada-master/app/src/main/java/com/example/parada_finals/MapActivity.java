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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private BottomNavigationView bottomNavigation;
    private String username;
    private EditText etOriginMap, etDestMap;
    private List<Marker> allMarkers = new ArrayList<>();
    private Map<String, GeoPoint> locationPoints = new HashMap<>();
    private MyLocationNewOverlay myLocationOverlay;
    private Polyline currentRouteLine;
    private CardView cardDistanceOverlay;
    private TextView tvOverlayDistance;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);

        username = getIntent().getStringExtra("USERNAME");

        TextView tvUsernameHeader = findViewById(R.id.tvUsernameHeader);
        if (tvUsernameHeader != null) {
            tvUsernameHeader.setText(username != null ? username : "hans");
        }

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        cardDistanceOverlay = findViewById(R.id.cardDistanceOverlay);
        tvOverlayDistance = findViewById(R.id.tvOverlayDistance);

        GeoPoint zamboanga = new GeoPoint(6.9214, 122.0790);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(zamboanga);

        checkLocationPermissions();
        initializeMarkers();

        etOriginMap = findViewById(R.id.etOriginMap);
        etDestMap = findViewById(R.id.etDestMap);

        etOriginMap.setFocusable(false);
        etOriginMap.setOnClickListener(v -> showLocationPickerDialog(etOriginMap));

        etDestMap.setFocusable(false);
        etDestMap.setOnClickListener(v -> showLocationPickerDialog(etDestMap));

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
            if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();
                return true;
            }
            return id == R.id.nav_map;
        });
    }

    private void checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        addMarker(6.9174, 122.0754, "KCC Mall de Zamboanga", "Gov. Camins Ave", "Mall", "img_7");
        addMarker(6.9093, 122.0753, "ADZU", "La Purisima St", "University", "img_8");
        addMarker(6.9400, 122.0488, "Pasonanca Park", "Pasonanca", "Park", "img_7");
        addMarker(6.9004, 122.0825, "Fort Pilar Shrine", "NS Valderosa St", "History", "img_8");
        addMarker(6.9248, 122.0594, "Zamboanga Airport", "Moret IT", "Airport", "img_7");
        addMarker(6.9090, 122.0750, "SM Mindpro", "La Purisima St", "Mall", "img_8");
        addMarker(6.9156, 122.0619, "WMSU", "Normal Rd", "University", "img_7");
        addMarker(6.9126, 122.0560, "Grandstand", "San Jose", "Sports", "img_8");
        addMarker(6.9061, 122.0748, "Pilar College", "Justice RT Lim Blvd", "School", "img_7");
        addMarker(6.9284, 122.0467, "Yubenco Gusu", "San Jose Gusu", "Mall", "img_8");
        addMarker(6.9183, 122.0838, "Yubenco Tetuan", "Tetuan", "Mall", "img_7");
        addMarker(6.9651, 121.9829, "Yubenco Ayala", "Ayala", "Mall", "img_8");
        addMarker(6.9265, 122.0612, "Garden Orchid", "Gov. Camins Ave", "Hotel", "img_7");

        addMarker(6.8920, 122.1080, "Arena Blanco", "Brgy. Arena Blanco", "Coastal", "img_8");
        addMarker(6.9634, 121.9774, "Ayala", "Brgy. Ayala", "Hub", "img_7");
        addMarker(6.9130, 122.0620, "Baliwasan", "Brgy. Baliwasan", "Residential", "img_8");
        addMarker(6.9530, 122.1330, "Boalan", "Brgy. Boalan", "Developing", "img_7");
        addMarker(7.0850, 122.2220, "Bolong", "Brgy. Bolong", "Beach", "img_8");
        addMarker(7.1550, 122.1850, "Bunguiao", "Brgy. Bunguiao", "Calm", "img_7");
        addMarker(7.0140, 122.1580, "Cabaluay", "Brgy. Cabaluay", "Coastal", "img_8");
        addMarker(6.9360, 122.0640, "Cabatangan", "Brgy. Cabatangan", "Views", "img_7");
        addMarker(6.9320, 122.0400, "Calarian", "Brgy. Calarian", "Golf", "img_8");
        addMarker(6.9140, 122.0720, "Camino Nuevo", "Brgy. Camino Nuevo", "Central", "img_7");
        addMarker(6.9060, 122.0600, "Campo Islam", "Brgy. Campo Islam", "Historic", "img_8");
        addMarker(6.9150, 122.0680, "Canelar", "Brgy. Canelar", "Barter", "img_7");
        addMarker(6.9650, 122.0420, "Capisan", "Brgy. Capisan", "Cool", "img_8");
        addMarker(6.9680, 122.1450, "Culianan", "Brgy. Culianan", "Agri", "img_7");
        addMarker(7.2810, 122.2030, "Curuan", "Brgy. Curuan", "Hub", "img_8");
        addMarker(6.9320, 122.0910, "Guiwan", "Brgy. Guiwan", "Hub", "img_7");
        addMarker(7.0370, 122.0120, "La Paz", "Brgy. La Paz", "Scenic", "img_8");
        addMarker(7.0420, 121.8980, "Labuan", "Brgy. Labuan", "Coastal", "img_7");
        addMarker(7.0780, 121.8740, "Limpapa", "Brgy. Limpapa", "Boundary", "img_8");
        addMarker(6.9640, 122.1070, "Lumbangan", "Brgy. Lumbangan", "Active", "img_7");
        addMarker(6.9580, 122.0830, "Lunzuran", "Brgy. Lunzuran", "Peaceful", "img_8");
        addMarker(6.9580, 121.9960, "Maasin", "Brgy. Maasin", "River", "img_7");
        addMarker(6.9200, 122.1150, "Mampang", "Brgy. Mampang", "Salt", "img_8");
        addMarker(6.9825, 122.1353, "Mercedes", "Brgy. Mercedes", "Residential", "img_7");
        addMarker(6.9940, 122.1510, "Pasobolong", "Brgy. Pasobolong", "Agri", "img_8");
        addMarker(7.0580, 121.8880, "Patalon", "Brgy. Patalon", "Scenic", "img_7");
        addMarker(6.9090, 122.0630, "San Jose Cawa-Cawa", "Brgy. San Jose", "Boulevard", "img_8");
        addMarker(6.9284, 122.0467, "San Jose Gusu", "Brgy. San Jose Gusu", "Busy", "img_7");
        addMarker(7.0000, 121.9210, "San Ramon", "Brgy. San Ramon", "Penal", "img_8");
        addMarker(6.9250, 122.0710, "San Roque", "Brgy. San Roque", "Large", "img_7");
        addMarker(7.0850, 122.1930, "Sangali", "Brgy. Sangali", "Port", "img_8");
        addMarker(6.9080, 122.0790, "Santa Barbara", "Brgy. Sta. Barbara", "Historic", "img_7");
        addMarker(6.9050, 122.0860, "Santa Catalina", "Brgy. Sta. Catalina", "Vibrant", "img_8");
        addMarker(6.9310, 122.0740, "Santa Maria", "Brgy. Sta. Maria", "Large", "img_7");
        addMarker(6.9140, 122.0580, "Santo Niño", "Brgy. Santo Niño", "Community", "img_8");
        addMarker(6.9402, 122.0104, "Sinunoc", "Brgy. Sinunoc", "Scenic", "img_7");
        addMarker(7.0228, 121.9192, "Sinubong", "Brgy. Sinubong", "Pickup", "img_8");
        addMarker(6.9876, 121.9296, "Talisayan", "Brgy. Talisayan", "Beach", "img_7");
        addMarker(6.9110, 122.1020, "Talon-Talon", "Brgy. Talon-Talon", "Vibrant", "img_8");
        addMarker(6.9183, 122.0838, "Tetuan", "Brgy. Tetuan", "Major", "img_7");
        addMarker(6.9160, 122.0960, "Tugbungan", "Brgy. Tugbungan", "Residential", "img_8");
        addMarker(7.3780, 122.2850, "Vitali", "Brgy. Vitali", "North", "img_7");
        addMarker(6.9550, 122.1240, "Zambowood", "Brgy. Zambowood", "Peaceful", "img_8");
    }

    private void addMarker(double lat, double lon, String title, String addr, String desc, String img) {
        GeoPoint point = new GeoPoint(lat, lon);
        locationPoints.put(title, point);

        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSnippet(addr + "|" + desc + "|" + img);
        marker.setOnMarkerClickListener((m, mapView) -> {
            showPlaceDetails(m);
            return true;
        });
        mapView.getOverlays().add(marker);
        allMarkers.add(marker);
    }

    private void showLocationPickerDialog(EditText targetField) {
        List<String> names = new ArrayList<>(locationPoints.keySet());
        names.add(0, "Current Location");
        Collections.sort(names.subList(1, names.size()));

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
        LocationAdapter adapter = new LocationAdapter(names, locationName -> {
            targetField.setText(locationName);
            checkAndDrawRoute();
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

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showPlaceDetails(Marker m) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_place_details, null);
        
        String[] parts = m.getSnippet().split("\\|");
        
        ((TextView) view.findViewById(R.id.tvPlaceTitle)).setText(m.getTitle());
        
        GeoPoint markerPos = m.getPosition();
        String distanceText = "Calculating...";
        if (myLocationOverlay != null && myLocationOverlay.getMyLocation() != null) {
            GeoPoint userPos = myLocationOverlay.getMyLocation();
            float[] results = new float[1];
            Location.distanceBetween(userPos.getLatitude(), userPos.getLongitude(),
                    markerPos.getLatitude(), markerPos.getLongitude(), results);
            float distM = results[0];
            distanceText = distM < 1000 ? String.format("%.0f m away", distM) : String.format("%.2f km away", distM / 1000.0);
        }

        ((TextView) view.findViewById(R.id.tvPlaceAddress)).setText(parts[0] + "\n\n" + parts[1] + "\n\n📍 " + distanceText);

        AlertDialog dialog = builder.setView(view).create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        view.findViewById(R.id.btnViewRoute).setOnClickListener(v -> {
            etDestMap.setText(m.getTitle());
            etOriginMap.setText("Current Location");
            checkAndDrawRoute();
            dialog.dismiss();
        });

        view.findViewById(R.id.btnCheckFare).setOnClickListener(v -> {
            Intent intent = new Intent(this, RoutesActivity.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("DESTINATION", m.getTitle());
            intent.putExtra("ORIGIN", "Current Location"); 
            startActivity(intent);
            finish();
        });

        view.findViewById(R.id.btnCloseModal).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void checkAndDrawRoute() {
        String origin = etOriginMap.getText().toString();
        String dest = etDestMap.getText().toString();

        if (origin.isEmpty() || dest.isEmpty()) return;

        GeoPoint startPoint = null;
        if (origin.equals("Current Location")) {
            startPoint = (myLocationOverlay != null) ? myLocationOverlay.getMyLocation() : null;
        } else {
            startPoint = locationPoints.get(origin);
        }

        GeoPoint endPoint = locationPoints.get(dest);

        if (startPoint == null || endPoint == null) {
            if (origin.equals("Current Location") && startPoint == null) {
                Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (currentRouteLine != null) mapView.getOverlays().remove(currentRouteLine);

        currentRouteLine = new Polyline();
        currentRouteLine.addPoint(startPoint);
        currentRouteLine.addPoint(endPoint);
        currentRouteLine.getOutlinePaint().setColor(Color.RED);
        currentRouteLine.getOutlinePaint().setStrokeWidth(10f);
        
        mapView.getOverlays().add(currentRouteLine);
        
        ArrayList<GeoPoint> points = new ArrayList<>();
        points.add(startPoint);
        points.add(endPoint);
        mapView.zoomToBoundingBox(org.osmdroid.util.BoundingBox.fromGeoPoints(points), true);

        double dist = startPoint.distanceToAsDouble(endPoint) / 1000.0;
        tvOverlayDistance.setText(String.format("Distance: %.2f km", dist));
        cardDistanceOverlay.setVisibility(View.VISIBLE);
        
        mapView.invalidate();
    }

    @Override protected void onResume() { super.onResume(); mapView.onResume(); if (myLocationOverlay != null) myLocationOverlay.enableMyLocation(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); if (myLocationOverlay != null) myLocationOverlay.disableMyLocation(); }
}
