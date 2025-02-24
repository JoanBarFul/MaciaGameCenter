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
        banners.add(new Banner("2024", R.drawable.banner_2024));
        banners.add(new Banner("Dungeon", R.drawable.banner_dungeon));
    
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
        String[] projection = {
            DatabaseHelper.COLUMN_USERNAME,
            DatabaseHelper.COLUMN_PROFILE_IMAGE,
            DatabaseHelper.COLUMN_LEVEL,
            DatabaseHelper.COLUMN_XP
        };
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };

        try {
            Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            );

            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_IMAGE));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEVEL));
                
                binding.usernameText.setText(name);
                binding.userLevel.setText("Nivel " + level);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading user data", e);
        } finally {
            db.close();
        }
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