package com.flaviums.sportshub.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;
import com.flaviums.sportshub.database.DatabaseHelper;

/**
 * AdminLoginActivity provides authentication for the organizing committee.
 * Features:
 * - Validates credentials against SQLite admin table via DatabaseHelper.checkAdminLogin
 * - Demonstrates Toast messaging for success/error states
 */
public class AdminLoginActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private EditText etAdminUsername;
    private EditText etAdminPassword;
    private Button btnLogin;
    private LinearLayout llLoginFormCard;
    private LinearLayout llAdminDashboard;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        databaseHelper = new DatabaseHelper(this);

        etAdminUsername = findViewById(R.id.etAdminUsername);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnLogin = findViewById(R.id.btnLogin);
        llLoginFormCard = findViewById(R.id.llLoginFormCard);
        llAdminDashboard = findViewById(R.id.llAdminDashboard);
        btnLogout = findViewById(R.id.btnLogout);

        // Pre-fill demo username for convenience
        etAdminUsername.setText("admin");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etAdminUsername.getText().toString().trim();
                String password = etAdminPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AdminLoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check credentials in SQLite database
                boolean isValid = databaseHelper.checkAdminLogin(username, password);
                if (isValid) {
                    Toast.makeText(AdminLoginActivity.this, "Authentication Successful! Welcome, " + username, Toast.LENGTH_LONG).show();
                    llLoginFormCard.setVisibility(View.GONE);
                    llAdminDashboard.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Invalid credentials. Try: admin / admin123", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llAdminDashboard.setVisibility(View.GONE);
                llLoginFormCard.setVisibility(View.VISIBLE);
                etAdminPassword.setText("");
                Toast.makeText(AdminLoginActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
