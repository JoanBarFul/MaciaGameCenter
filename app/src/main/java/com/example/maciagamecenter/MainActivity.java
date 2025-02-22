package com.example.maciagamecenter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.maciagamecenter.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup RecyclerView
        binding.gamesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // TODO: Set up games adapter

        // Setup Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Handle home
                return true;
            } else if (itemId == R.id.navigation_dashboard) {
                // Handle games
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                // Handle profile
                return true;
            }
            return false;
        });
    }
}