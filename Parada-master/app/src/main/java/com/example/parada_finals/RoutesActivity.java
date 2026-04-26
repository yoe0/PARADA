package com.example.parada_finals;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RoutesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    EditText searchBar;
    LinearLayout routeContainer;
    Button btnAddRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        // Initialize Views
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchBar = findViewById(R.id.searchBar);
        routeContainer = findViewById(R.id.routeContainer);
        btnAddRoute = findViewById(R.id.btnAddRoute);

        // Setup Add Button
        btnAddRoute.setOnClickListener(v -> showDialog(null));

        // Sync the sample card already in XML with the Edit/Delete logic
        applyActionsToCards();

        // 🔍 SEARCH LOGIC
        searchBar.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRoutes(s.toString());
            }
            public void afterTextChanged(Editable s) {}
        });

        // 🗺️ NAVIGATION LOGIC
        bottomNavigationView.setSelectedItemId(R.id.nav_routes);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_how_to) {
                startActivity(new Intent(this, LandingActivity.class));
                finish();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
            } else if (id == R.id.nav_map) {
                startActivity(new Intent(this, MapActivity.class));
                finish();
            }
            return true;
        });
    }

    // 🔍 FILTER CARDS BASED ON SEARCH
    private void filterRoutes(String text) {
        String query = text.toLowerCase().trim();
        for (int i = 0; i < routeContainer.getChildCount(); i++) {
            View card = routeContainer.getChildAt(i);

            // Try to find the title/route text to filter
            TextView tvRoute = card.findViewById(R.id.tvRoute);
            // If the card doesn't have an ID (like the old sample), find the first TextView
            if (tvRoute == null && card instanceof LinearLayout) {
                tvRoute = (TextView) ((LinearLayout) card).getChildAt(0);
            }

            if (tvRoute != null) {
                String routeName = tvRoute.getText().toString().toLowerCase();
                card.setVisibility(routeName.contains(query) ? View.VISIBLE : View.GONE);
            }
        }
    }

    // 📌 SHOW DIALOG (Handles both Adding and Editing)
    private void showDialog(View cardToEdit) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_route, null);

        EditText etRoute = view.findViewById(R.id.etRoute);
        EditText etVehicle = view.findViewById(R.id.etVehicle);
        EditText etFare = view.findViewById(R.id.etFare);
        EditText etTime = view.findViewById(R.id.etTime);

        // PRE-FILL DATA IF EDITING
        if (cardToEdit != null) {
            TextView tvRoute = cardToEdit.findViewById(R.id.tvRoute);
            TextView tvVehicle = cardToEdit.findViewById(R.id.tvVehicle);
            TextView tvFare = cardToEdit.findViewById(R.id.tvFare);
            TextView tvTime = cardToEdit.findViewById(R.id.tvTime);

            if (tvRoute != null) etRoute.setText(tvRoute.getText());
            if (tvVehicle != null) etVehicle.setText(tvVehicle.getText().toString().replace("Vehicle: ", ""));
            if (tvFare != null) etFare.setText(tvFare.getText().toString().replace("Fare: ", ""));
            if (tvTime != null) etTime.setText(tvTime.getText());
        }

        new AlertDialog.Builder(this)
                .setTitle(cardToEdit == null ? "Add Route" : "Edit Route")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {
                    String route = etRoute.getText().toString();
                    String vehicle = etVehicle.getText().toString();
                    String fare = etFare.getText().toString();
                    String time = etTime.getText().toString();

                    if (cardToEdit == null) {
                        addNewCard(route, vehicle, fare, time);
                    } else {
                        // UPDATE EXISTING VIEWS
                        TextView tvR = cardToEdit.findViewById(R.id.tvRoute);
                        TextView tvV = cardToEdit.findViewById(R.id.tvVehicle);
                        TextView tvF = cardToEdit.findViewById(R.id.tvFare);
                        TextView tvT = cardToEdit.findViewById(R.id.tvTime);

                        if (tvR != null) tvR.setText(route);
                        if (tvV != null) tvV.setText("Vehicle: " + vehicle);
                        if (tvF != null) tvF.setText("Fare: " + fare);
                        if (tvT != null) tvT.setText(time);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ➕ ADD NEW CARD FROM TEMPLATE
    private void addNewCard(String route, String vehicle, String fare, String time) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_route, routeContainer, false);

        ((TextView) card.findViewById(R.id.tvRoute)).setText(route);
        ((TextView) card.findViewById(R.id.tvVehicle)).setText("Vehicle: " + vehicle);
        ((TextView) card.findViewById(R.id.tvFare)).setText("Fare: " + fare);
        ((TextView) card.findViewById(R.id.tvTime)).setText(time);

        card.findViewById(R.id.btnEdit).setOnClickListener(v -> showDialog(card));
        card.findViewById(R.id.btnDelete).setOnClickListener(v -> routeContainer.removeView(card));

        routeContainer.addView(card);
    }

    // ✏️ APPLY ACTIONS TO THE SAMPLE CARD IN XML
    private void applyActionsToCards() {
        for (int i = 0; i < routeContainer.getChildCount(); i++) {
            View card = routeContainer.getChildAt(i);

            // Try finding buttons by the IDs used in the activity_routes.xml
            View edit = card.findViewById(R.id.btnEdit);
            View delete = card.findViewById(R.id.btnDelete);

            if (edit != null) {
                edit.setOnClickListener(v -> showDialog(card));
            }
            if (delete != null) {
                delete.setOnClickListener(v -> routeContainer.removeView(card));
            }
        }
    }
}