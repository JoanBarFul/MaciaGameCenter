package com.example.maciagamecenter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // Añadimos este import
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maciagamecenter.database.DatabaseHelper;
import com.example.maciagamecenter.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.loginButton.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            Log.d("LoginActivity", "Intento de login - Username: " + username + ", Password: " + password);

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            if (databaseHelper.checkUser(username, password)) {
                Log.d("LoginActivity", "Login exitoso");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.d("LoginActivity", "Login fallido");
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        binding.registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}