package com.flaviums.sportshub.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;


public class AboutActivity extends AppCompatActivity {

    // TODO: Replace placeholder URL with official NMIMS MPSTME sports fest portal once deployed
    private static final String COLLEGE_URL = "https://www.nmims.edu";

    private WebView webView;
    private LinearLayout llFallbackContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        webView = findViewById(R.id.webView);
        llFallbackContainer = findViewById(R.id.llFallbackContainer);

        // Check internet connection
        if (!isNetworkAvailable()) {
            showOfflineFallback("No internet connection detected.");
            return;
        }

        // Configure WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Trigger offline fallback on network or URL load failure
                showOfflineFallback("Failed to load college webpage.");
            }
        });

        // Load college website
        webView.loadUrl(COLLEGE_URL);
    }

    /**
     * Helper to verify network connectivity.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    /**
     * Displays offline text fallback and displays a Toast notification.
     */
    private void showOfflineFallback(String reason) {
        webView.setVisibility(View.GONE);
        llFallbackContainer.setVisibility(View.VISIBLE);
        Toast.makeText(this, reason + " Displaying offline Flaviums info.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        // Allow in-page navigation if WebView can go back
        if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
