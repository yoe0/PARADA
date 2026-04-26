package com.example.parada_finals;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize only the navigation elements
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        // 1. Direct to Landing Page on Login click
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LandingActivity.class);
            startActivity(intent);
            // Optional: finish(); if you want to prevent going back to login
        });

        // 2. Direct to Register Page on "Create one" click
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


    }
}