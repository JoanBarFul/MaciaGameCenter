package com.example.maciagamecenter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.maciagamecenter.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.maciagamecenter.database.DatabaseHelper;
import android.content.SharedPreferences;
import android.content.Intent;  // Añadir este import
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import java.util.List;
import com.bumptech.glide.Glide;  // Añadir este import
import com.bumptech.glide.load.engine.DiskCacheStrategy;  // Añadir este import

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DatabaseHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    
        dbHelper = new DatabaseHelper(this);
        loadUserData();
        setupNavigation();
        // Inicializar lista de banners
        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner("", R.drawable.banner2048));  // Cambiado de banner_2024 a banner2048
        banners.add(new Banner("", R.drawable.gungeongamebanner));
    // Configurar RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1; // Cada item ocupa 1 espacio (2 columnas)
            }
        });
        binding.gamesRecyclerView.setLayoutManager(layoutManager);
        BannerAdapter adapter = new BannerAdapter(banners);
        binding.gamesRecyclerView.setAdapter(adapter);
    

    }
    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = prefs.getString("currentUser", "");
        
        if (username.isEmpty()) {
            if (!isFinishing()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            return;
        }
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            new String[]{DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_LEVEL, DatabaseHelper.COLUMN_PROFILE_IMAGE},
            DatabaseHelper.COLUMN_USERNAME + "=?",
            new String[]{username},
            null, null, null
        );

        if (cursor.moveToFirst()) {
            // Usar la variable username que ya existe en lugar de crear una nueva
            binding.usernameText.setText(username);
            int level = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL));
            
            Log.d("MainActivity", "Username: " + username);
            Log.d("MainActivity", "Level: " + level);
            
            binding.userLevel.setText("Nivel " + level);
            
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_IMAGE));
            Log.d("MainActivity", "Image URI: " + imageUri);
            
            // Establecer imagen por defecto
            binding.profileImage.setImageResource(R.drawable.default_profile);
            
            if (imageUri != null && !imageUri.isEmpty()) {
                try {
                    android.net.Uri uri = android.net.Uri.parse(imageUri);
                    Log.d("MainActivity", "Loading image with URI: " + uri);

                    // Usar Glide directamente como en las otras actividades
                    Glide.with(this)
                        .load(uri)
                        .circleCrop()  // Usar circleCrop() como en las otras actividades
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(binding.profileImage);
                    
                } catch (Exception e) {
                    Log.e("MainActivity", "Error loading image: " + e.getMessage());
                    binding.profileImage.setImageResource(R.drawable.default_profile);
                }
            }
        }
        cursor.close();
        db.close();
    }
    
    // Add this method to handle game launches
    private void setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_leaderboard) {
                startActivity(new Intent(this, LeaderboardActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
} // Cierre de la clase MainActivity