package com.example.maciagamecenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.maciagamecenter.database.DatabaseHelper;
import com.example.maciagamecenter.databinding.ActivityProfileBinding;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import android.content.ContentValues;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private DatabaseHelper dbHelper;
    private static final int REQUEST_IMAGE_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DatabaseHelper(this);
        loadUserProfile();
        loadStatistics();
        setupNavigation();
        setupConfigurationButtons();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUser", "");
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            new String[]{DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_LEVEL, DatabaseHelper.COLUMN_PROFILE_IMAGE},
            DatabaseHelper.COLUMN_USERNAME + "=?",
            new String[]{username},
            null, null, null
        );

        if (cursor.moveToFirst()) {
            binding.usernameText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)));
            binding.userLevel.setText("Nivel " + cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL)));
            
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_IMAGE));
            if (imageUri != null && !imageUri.isEmpty()) {
                Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(binding.profileImage);
            }
        }
        cursor.close();
    }

    private void loadStatistics() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUser", "");
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) as games_played, SUM(score) as total_score " +
            "FROM " + DatabaseHelper.TABLE_GAME_SCORES +
            " WHERE " + DatabaseHelper.COLUMN_PLAYER_NAME + "=?",
            new String[]{username}
        );

        if (cursor.moveToFirst()) {
            binding.totalGamesPlayed.setText(String.valueOf(cursor.getInt(0)));
            binding.totalScore.setText(String.valueOf(cursor.getInt(1)));
        }
        cursor.close();
    }

    private void setupNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_profile);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_leaderboard) {
                startActivity(new Intent(this, LeaderboardActivity.class));
                finish();
                return true;
            }
            return true;
        });
    }
    private void setupConfigurationButtons() {
        binding.changePasswordButton.setOnClickListener(v -> {
            showChangePasswordDialog();
        });

        binding.changeImageButton.setOnClickListener(v -> {
            selectImage();
        });

        binding.deleteDataButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });
        binding.addScoreButton.setOnClickListener(v -> {
            showAddScoreDialog();
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText currentPassword = view.findViewById(R.id.currentPassword);
        EditText newPassword = view.findViewById(R.id.newPassword);
        EditText confirmPassword = view.findViewById(R.id.confirmPassword);

        builder.setView(view)
               .setTitle("Cambiar contraseña")
               .setPositiveButton("Cambiar", (dialog, which) -> {
                   String current = currentPassword.getText().toString();
                   String newPass = newPassword.getText().toString();
                   String confirm = confirmPassword.getText().toString();
                   
                   if (validatePasswordChange(current, newPass, confirm)) {
                       updatePassword(newPass);
                   }
               })
               .setNegativeButton("Cancelar", null)
               .show();
    }
    private void showAddScoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_score, null);
        
        Spinner gameSpinner = view.findViewById(R.id.gameSpinner);
        EditText scoreInput = view.findViewById(R.id.scoreInput);

        // Configurar el spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            new String[]{"2024", "Dungeon"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameSpinner.setAdapter(adapter);

        builder.setView(view)
               .setTitle("Añadir puntuación")
               .setPositiveButton("Guardar", (dialog, which) -> {
                   String game = gameSpinner.getSelectedItem().toString();
                   String scoreStr = scoreInput.getText().toString();
                   
                   if (!scoreStr.isEmpty()) {
                       int score = Integer.parseInt(scoreStr);
                       saveScore(game, score);
                   }
               })
               .setNegativeButton("Cancelar", null)
               .show();
    }

    private void saveScore(String game, int score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUser", "");

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PLAYER_NAME, username);
        values.put(DatabaseHelper.COLUMN_GAME_NAME, game);
        values.put(DatabaseHelper.COLUMN_SCORE, score);

        long newRowId = db.insert(DatabaseHelper.TABLE_GAME_SCORES, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Puntuación guardada correctamente", Toast.LENGTH_SHORT).show();
            loadStatistics(); // Actualizar estadísticas
        } else {
            Toast.makeText(this, "Error al guardar la puntuación", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean validatePasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUser", "");
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            new String[]{DatabaseHelper.COLUMN_PASSWORD},
            DatabaseHelper.COLUMN_USERNAME + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?",
            new String[]{username, currentPassword},
            null, null, null
        );

        boolean isValid = cursor.moveToFirst();
        cursor.close();

        if (!isValid) {
            Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void updatePassword(String newPassword) {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUser", "");
        
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PASSWORD, newPassword);

        int rowsAffected = db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            DatabaseHelper.COLUMN_USERNAME + "=?",
            new String[]{username}
        );

        if (rowsAffected > 0) {
            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
                String imageUri = data.getData().toString();
                
                // Actualizar imagen en la base de datos
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_PROFILE_IMAGE, imageUri);
        
                SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String username = prefs.getString("currentUser", "");
        
                db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    DatabaseHelper.COLUMN_USERNAME + "=?",
                    new String[]{username}
                );
        
                // Mostrar la nueva imagen
                Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(binding.profileImage);
        
                Toast.makeText(this, "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show();
            }
        }
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Borrar datos")
            .setMessage("¿Estás seguro de que quieres borrar todos tus datos? Esta acción no se puede deshacer.")
            .setPositiveButton("Borrar", (dialog, which) -> {
                deleteUserData();
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteUserData() {
        // Implementar borrado de datos
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUser", "");

        db.delete(DatabaseHelper.TABLE_GAME_SCORES, 
                 DatabaseHelper.COLUMN_PLAYER_NAME + "=?", 
                 new String[]{username});

        // Cerrar sesión
        prefs.edit().clear().apply();
        
        // Volver a la pantalla de login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}