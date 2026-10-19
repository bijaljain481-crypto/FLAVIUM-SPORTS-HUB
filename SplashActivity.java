package com.flaviums.sportshub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;

/**
 * SplashActivity serves as the launcher activity for Flaviums Sports Hub.
 * Features:
 * - Full-screen gradient background (defined in res/drawable/splash_gradient.xml)
 * - Centered application title and NMIMS MPSTME branding
 * - Placeholder logo ImageView (id: ivLogo) ready for official college emblem
 * - 2-second delay using Android Handler before launching MainActivity via an explicit Intent
 */
public class SplashActivity extends AppCompatActivity {

    // Splash display duration in milliseconds (2 seconds)
    private static final int SPLASH_DURATION_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content view using the XML layout with gradient background
        setContentView(R.layout.activity_splash);

        // TODO: Replace @drawable/ic_placeholder_logo with the official SVKM/NMIMS MPSTME logo PNG once added to res/drawable
        ImageView ivLogo = findViewById(R.id.ivLogo);

        // Use Handler with main Looper to pause for 2 seconds, then transition to MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Explicit Intent to navigate from SplashActivity to MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // Finish SplashActivity so the user cannot navigate back to splash screen via back button
                finish();
            }
        }, SPLASH_DURATION_MS);
    }
}
