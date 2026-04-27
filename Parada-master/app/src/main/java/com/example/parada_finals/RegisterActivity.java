package com.example.parada_finals;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegPassword, etRegConfirmPassword;
    private ImageView ivShowRegPassword, ivShowConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        ivShowRegPassword = findViewById(R.id.ivShowRegPassword);
        ivShowConfirmPassword = findViewById(R.id.ivShowConfirmPassword);

        // Show/Hide Password logic
        ivShowRegPassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowRegPassword.setImageResource(android.R.drawable.ic_menu_view);
                isPasswordVisible = false;
            } else {
                etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowRegPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                isPasswordVisible = true;
            }
            etRegPassword.setSelection(etRegPassword.getText().length());
        });

        // Show/Hide Confirm Password logic
        ivShowConfirmPassword.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                etRegConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_view);
                isConfirmPasswordVisible = false;
            } else {
                etRegConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                isConfirmPasswordVisible = true;
            }
            etRegConfirmPassword.setSelection(etRegConfirmPassword.getText().length());
        });

        btnRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Registration Successful (Demo)", Toast.LENGTH_LONG).show();
            // Go back to login
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}