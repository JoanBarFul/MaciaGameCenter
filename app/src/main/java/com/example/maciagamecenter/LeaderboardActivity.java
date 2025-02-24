package com.example.maciagamecenter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.maciagamecenter.database.DatabaseHelper;
import com.example.maciagamecenter.databinding.ActivityLeaderboardBinding;
import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.content.SharedPreferences;  // Añadir este import
import com.bumptech.glide.Glide;  // Añadir este import también

public class LeaderboardActivity extends AppCompatActivity {
    private ActivityLeaderboardBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ocultar la ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DatabaseHelper(this);
        loadUserProfile();  // Añadir esta línea
        setupGameButtons();
        loadScores("2048"); // Cambiar "2024" por "2048"
        
        // Configurar BottomNavigationView
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_leaderboard);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return true;
        });
    }

    private void setupGameButtons() {
        binding.game2024Button.setOnClickListener(v -> loadScores("2048")); // Cambiar "2024" por "2048"
        binding.gameDungeonButton.setOnClickListener(v -> loadScores("Dungeon"));
    }

    private void loadScores(String gameName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_GAME_SCORES,
            null,
            DatabaseHelper.COLUMN_GAME_NAME + "=?",
            new String[]{gameName},
            null,
            null,
            DatabaseHelper.COLUMN_SCORE + " DESC",
            "10"
        );

        List<ScoreItem> scores = new ArrayList<>();
        while (cursor.moveToNext()) {
            scores.add(new ScoreItem(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLAYER_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE))
            ));
        }
        cursor.close();

        ScoresAdapter adapter = new ScoresAdapter(scores);
        binding.scoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.scoresRecyclerView.setAdapter(adapter);
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
}