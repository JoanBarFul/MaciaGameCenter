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
    
        // Setup RecyclerView
        binding.gamesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // Remove or comment out this section
        /*
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_leaderboard) {
                startActivity(new Intent(this, LeaderboardActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                return true;
            }
            return false;
        });
        */
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
            binding.usernameText.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)));
            binding.userLevel.setText("Nivel " + cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL)));
            
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_IMAGE));
            if (imageUri != null && !imageUri.isEmpty()) {
                try {
                    android.net.Uri uri = android.net.Uri.parse(imageUri);
                    // Añadir permisos de lectura para la URI
                    getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                    
                    Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.profileImage);
                    
                    Log.d("MainActivity", "Loading image from URI: " + imageUri);
                } catch (Exception e) {
                    Log.e("MainActivity", "Error loading image: " + e.getMessage());
                    binding.profileImage.setImageResource(R.drawable.default_profile);
                }
            } else {
                binding.profileImage.setImageResource(R.drawable.default_profile);
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