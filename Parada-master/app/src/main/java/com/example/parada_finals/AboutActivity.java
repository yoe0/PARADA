package com.example.parada_finals;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize back button
        btnBack = findViewById(R.id.btnBack);

        // Back action
        btnBack.setOnClickListener(v -> finish());
    }
}