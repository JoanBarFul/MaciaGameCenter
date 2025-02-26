package com.example.maciagamecenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;      
import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private VideoView splashVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Asegurarnos que la actividad es visible
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        
        splashVideo = findViewById(R.id.splashVideo);
        try {
            // Usar el recurso spalsh.webm
            int videoResourceId = getResources().getIdentifier("spalsh", "raw", getPackageName());
            Log.d("SplashActivity", "Video resource ID: " + videoResourceId);
            
            if (videoResourceId == 0) {
                Log.e("SplashActivity", "Video resource not found");
                checkLoginAndRedirect();
                return;
            }

            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId);
            Log.d("SplashActivity", "Video path: " + videoUri.toString());
            splashVideo.setVideoURI(videoUri);
            
            splashVideo.setOnPreparedListener(mp -> {
                Log.d("SplashActivity", "Video prepared, starting playback");
                mp.setLooping(false);
                splashVideo.start();
            });

            splashVideo.setOnErrorListener((mp, what, extra) -> {
                Log.e("SplashActivity", "Error playing video: what=" + what + " extra=" + extra);
                checkLoginAndRedirect();
                return true;
            });

            splashVideo.setOnCompletionListener(mp -> {
                Log.d("SplashActivity", "Video completed, redirecting");
                checkLoginAndRedirect();
            });

        } catch (Exception e) {
            Log.e("SplashActivity", "Error setting video: " + e.getMessage(), e);
            checkLoginAndRedirect();
        }
    }

    // Eliminamos el método goToLogin() ya que usaremos checkLoginAndRedirect()
    private void checkLoginAndRedirect() {
        // Siempre ir al login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (splashVideo != null && splashVideo.isPlaying()) {
            splashVideo.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashVideo != null) {
            splashVideo.stopPlayback();
        }
    }
}