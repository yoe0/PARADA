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
        addMarker(6.9174, 122.0754, "KCC Mall de Zamboanga", "Gov. Camins Ave", "Large shopping mall with dining and entertainment", "img_7");
        addMarker(6.9093, 122.0753, "ADZU", "La Purisima St", "Private university known for academic excellence", "img_8");
        addMarker(6.9400, 122.0488, "Pasonanca Park", "Pasonanca", "Popular park with pools and picnic areas", "img_7");
        addMarker(6.9004, 122.0825, "Fort Pilar Shrine", "NS Valderosa St", "Historic shrine and cultural landmark", "img_8");
        addMarker(6.9248, 122.0594, "Zamboanga Airport", "Moret IT", "Main airport serving the city", "img_7");
        addMarker(6.9090, 122.0750, "SM Mindpro", "La Purisima St", "Modern mall in the city center", "img_8");
        addMarker(6.9156, 122.0619, "WMSU", "Normal Rd", "State university with diverse programs", "img_7");
        addMarker(6.9126, 122.0560, "Grandstand", "San Jose", "Public venue for events and sports", "img_8");
        addMarker(6.9061, 122.0748, "Pilar College", "Justice RT Lim Blvd", "Well-known private school", "img_7");
        addMarker(6.9284, 122.0467, "Yubenco Gusu", "San Jose Gusu", "Local supermarket and shopping area", "img_8");
        addMarker(6.9183, 122.0838, "Yubenco Tetuan", "Tetuan", "Convenient shopping center in Tetuan", "img_7");
        addMarker(6.9651, 121.9829, "Yubenco Ayala", "Ayala", "Mall serving western barangays", "img_8");
        addMarker(6.9265, 122.0612, "Garden Orchid", "Gov. Camins Ave", "Hotel with events and dining services", "img_7");

        addMarker(6.919773034344579, 122.15343937022541, "Arena Blanco", "Brgy. Arena Blanco", "Coastal barangay with beaches and fishing areas", "img_8");
        addMarker(6.96373558093434, 121.94816715064628, "Ayala", "Brgy. Ayala", "Busy transport and commercial hub", "img_7");
        addMarker(6.915959560863099, 122.05998500908423, "Baliwasan", "Brgy. Baliwasan", "Residential area near city proper", "img_8");
        addMarker(6.952658391380117, 122.11848732442542, "Boalan", "Brgy. Boalan", "Growing residential and commercial zone", "img_7");
        addMarker(7.097771919216988, 122.24040953976699, "Bolong", "Brgy. Bolong", "Beach area known for resorts and seafood", "img_8");
        addMarker(7.107556004415077, 122.20027894767257, "Bunguiao", "Brgy. Bunguiao", "Quiet coastal community with scenic views", "img_7");
        addMarker(6.99535001119771, 122.17729443791978, "Cabaluay", "Brgy. Cabaluay", "Coastal barangay with resorts and fishing", "img_8");
        addMarker(6.943481595456743, 122.05737190908422, "Cabatangan", "Brgy. Cabatangan", "Residential area near airport zone", "img_7");
        addMarker(6.9243845553103265, 122.02980908209567, "Calarian", "Brgy. Calarian", "Home of golf course and seaside spots", "img_8");
        addMarker(6.914615272553048, 122.07337289876416, "Camino Nuevo", "Brgy. Camino Nuevo", "Central barangay with commercial activity", "img_7");
        addMarker(6.914864258228712, 122.0460553379195, "Campo Islam", "Brgy. Campo Islam", "Historic coastal community", "img_8");
        addMarker(6.9165683574969865, 122.0706138514137, "Canelar", "Brgy. Canelar", "Known for barter trade and markets", "img_7");
        addMarker(6.974283883154848, 122.03457646675494, "Capisan", "Brgy. Capisan", "Cool elevated area with greenery", "img_8");
        addMarker(6.973446925097431, 122.14670820297727, "Culianan", "Brgy. Culianan", "Agricultural hub with local markets", "img_7");
        addMarker(7.210296116702678, 122.23172212812018, "Curuan", "Brgy. Curuan", "Major transport and trading hub", "img_8");
        addMarker(6.928898798817435, 122.09211653976607, "Guiwan", "Brgy. Guiwan", "Urban barangay with mixed residential areas", "img_7");
        addMarker(6.9874008303006025, 121.95809296860172, "La Paz", "Brgy. La Paz", "Scenic rural area with farms", "img_8");
        addMarker(7.0982952055643045, 121.90299965326125, "Labuan", "Brgy. Labuan", "Coastal barangay with port access", "img_7");
        addMarker(7.14305704852182, 121.90252791093206, "Limpapa", "Brgy. Limpapa", "Boundary barangay near neighboring province", "img_8");
        addMarker(6.970478733340677, 122.10312333976624, "Lumbangan", "Brgy. Lumbangan", "Active residential and farming area", "img_7");
        addMarker(6.952279342122488, 122.09064333791947, "Lunzuran", "Brgy. Lunzuran", "Quiet community with residential homes", "img_8");
        addMarker(6.965875223422909, 121.98564167261706, "Maasin", "Brgy. Maasin", "Riverside barangay with agriculture", "img_7");
        addMarker(6.915846113260775, 122.13447833976619, "Mampang", "Brgy. Mampang", "Known for salt production and coastal life", "img_8");
        addMarker(6.9582373404550335, 122.14805194559018, "Mercedes", "Brgy. Mercedes", "Residential suburb with growing developments", "img_7");
        addMarker(6.9776803900728295, 122.12828080221642, "Pasobolong", "Brgy. Pasobolong", "Agricultural area with farms and fields", "img_8");
        addMarker(7.052948750702311, 121.90958083792019, "Patalon", "Brgy. Patalon", "Scenic area with hills and greenery", "img_7");
        addMarker(6.911551461401163, 122.06519119374309, "San Jose Cawa-Cawa", "Brgy. San Jose", "Boulevard area near the sea", "img_8");
        addMarker(6.908299192422751, 122.07635139575659, "San Jose Gusu", "Brgy. San Jose Gusu", "Busy urban center with markets", "img_7");
        addMarker(6.93078520358863, 122.04634125515484, "San Roque", "Brgy. San Roque", "Large residential barangay", "img_7");
        addMarker(7.078864084812816, 122.21381369887183, "Sangali", "Brgy. Sangali", "Port area with ferry connections", "img_8");
        addMarker(6.90358190569268, 122.08195263791943, "Santa Barbara", "Brgy. Sta. Barbara", "Historic area with cultural sites", "img_7");
        addMarker(6.909225909721007, 122.08696331093078, "Santa Catalina", "Brgy. Sta. Catalina", "Vibrant community near city center", "img_8");
        addMarker(6.93179326275812, 122.07474627159064, "Santa Maria", "Brgy. Sta. Maria", "Large residential area", "img_7");
        addMarker(7.033107105218401, 122.03946021633, "Santo Niño", "Brgy. Santo Niño", "Community with suburban feel", "img_8");
        addMarker(6.934310848424623, 122.00106731093099, "Sinunoc", "Brgy. Sinunoc", "Scenic coastal and farming area", "img_7");
        addMarker(7.023001649854029, 121.91933236675514, "Sinubong", "Brgy. Sinubong", "Pickup and transport point", "img_8");
        addMarker(6.987823377594933, 121.92978945326065, "Talisayan", "Brgy. Talisayan", "Beach area with resorts", "img_7");
        addMarker(6.90975476225303, 122.1123403839424, "Talon-Talon", "Brgy. Talon-Talon", "Busy residential and commercial area", "img_8");
        addMarker(6.917908009117975, 122.09089715326036, "Tetuan", "Brgy. Tetuan", "Major barangay with schools and markets", "img_7");
        addMarker(6.919832110919491, 122.10460962442515, "Tugbungan", "Brgy. Tugbungan", "Residential area with local businesses", "img_8");
        addMarker(7.376906813442156, 122.29004090908656, "Vitali", "Brgy. Vitali", "Northernmost barangay with rural setting", "img_7");
        addMarker(6.9414561961921795, 122.1325018802491, "Zambowood", "Brgy. Zambowood", "Peaceful residential community", "img_8");
        addMarker(7.0000, 121.9210, "San Ramon", "Brgy. San Ramon", "Known for penal colony and coastal area", "img_8");
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
