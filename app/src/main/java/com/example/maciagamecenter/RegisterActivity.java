package com.example.maciagamecenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.maciagamecenter.database.DatabaseHelper;
import com.example.maciagamecenter.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private DatabaseHelper databaseHelper;
    private int selectedImageResource = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        // Configurar selección de imágenes
        binding.profileImage1.setOnClickListener(v -> {
            selectedImageResource = R.drawable.profile_1;
            binding.selectedImageText.setText("Profile 1 selected");
            binding.profileImage1.setBackgroundResource(R.color.purple_500);
            binding.profileImage2.setBackgroundResource(R.drawable.image_border);
        });

        binding.profileImage2.setOnClickListener(v -> {
            selectedImageResource = R.drawable.profile_2;
            binding.selectedImageText.setText("Profile 2 selected");
            binding.profileImage2.setBackgroundResource(R.color.purple_500);
            binding.profileImage1.setBackgroundResource(R.drawable.image_border);
        });

        binding.registerButton.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();
            String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageResource == 0) {
                Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show();
                return;
            }

            String imageUri = "android.resource://" + getPackageName() + "/" + selectedImageResource;
            if (databaseHelper.addUser(username, password, imageUri)) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                databaseHelper.getAllUsers(); // Añadir esta línea
                finish();
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}