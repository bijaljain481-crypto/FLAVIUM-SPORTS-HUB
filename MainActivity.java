package com.flaviums.sportshub.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;
import com.flaviums.sportshub.database.DatabaseHelper;
import com.flaviums.sportshub.models.Match;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ViewFlipper vfTodayMatches;

    // Navigation Buttons
    private Button btnFixtures;
    private Button btnResults;
    private Button btnUpcoming;
    private Button btnPointsTable;
    private Button btnRules;
    private Button btnAbout;
    private Button btnAdminLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        // Bind UI Views
        vfTodayMatches = findViewById(R.id.vfTodayMatches);
        btnFixtures = findViewById(R.id.btnFixtures);
        btnResults = findViewById(R.id.btnResults);
        btnUpcoming = findViewById(R.id.btnUpcoming);
        btnPointsTable = findViewById(R.id.btnPointsTable);
        btnRules = findViewById(R.id.btnRules);
        btnAbout = findViewById(R.id.btnAbout);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        // Setup ViewFlipper with featured match cards and touch gesture support
        setupViewFlipper();

        // Setup Navigation Button Click Listeners (Explicit Intents)
        setupNavigationButtons();
    }


    private void setupViewFlipper() {
        vfTodayMatches.removeAllViews();

        // Retrieve upcoming or featured fixtures from database
        List<Match> matches = databaseHelper.getUpcomingMatches();
        if (matches == null || matches.isEmpty()) {
            matches = databaseHelper.getAllMatches();
        }

        float density = getResources().getDisplayMetrics().density;
        int p14 = (int) (14 * density);
        int p8 = (int) (8 * density);
        int p4 = (int) (4 * density);

        // Add up to 6 featured match cards into the ViewFlipper
        int limit = Math.min(6, matches.size());
        for (int i = 0; i < limit; i++) {
            final Match m = matches.get(i);

            // Card container
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.card_background);
            card.setPadding(p14, p14, p14, p14);
            card.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Top row: Sport Name + Status
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvSport = new TextView(this);
            tvSport.setText(m.getSportName());
            tvSport.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvSport.setTextSize(14);
            tvSport.setTypeface(null, Typeface.BOLD);
            tvSport.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            topRow.addView(tvSport);

            TextView tvBadge = new TextView(this);
            if ("Completed".equalsIgnoreCase(m.getStatus())) {
                tvBadge.setText("COMPLETED");
                tvBadge.setBackgroundResource(R.drawable.badge_completed);
            } else {
                tvBadge.setText("UPCOMING");
                tvBadge.setBackgroundResource(R.drawable.badge_upcoming);
            }
            tvBadge.setTextColor(Color.WHITE);
            tvBadge.setTextSize(11);
            tvBadge.setTypeface(null, Typeface.BOLD);
            tvBadge.setGravity(Gravity.CENTER);
            topRow.addView(tvBadge);

            card.addView(topRow);

            // Teams Headline
            TextView tvTeams = new TextView(this);
            tvTeams.setText(m.getTeamA() + "  vs  " + m.getTeamB());
            tvTeams.setTextColor(getResources().getColor(R.color.textPrimary));
            tvTeams.setTextSize(17);
            tvTeams.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams teamsParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            teamsParams.setMargins(0, p8, 0, 0);
            tvTeams.setLayoutParams(teamsParams);
            card.addView(tvTeams);

            // Schedule & Venue
            TextView tvMeta = new TextView(this);
            tvMeta.setText("📅 " + m.getDate() + " • ⏰ " + m.getTime() + " • 📍 " + m.getVenue());
            tvMeta.setTextColor(getResources().getColor(R.color.textSecondary));
            tvMeta.setTextSize(12);
            LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            metaParams.setMargins(0, p4, 0, 0);
            tvMeta.setLayoutParams(metaParams);
            card.addView(tvMeta);

            // Tap card to open MatchDetailActivity via explicit Intent
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MatchDetailActivity.class);
                    intent.putExtra("MATCH_ID", m.getId());
                    startActivity(intent);
                }
            });

            vfTodayMatches.addView(card);
        }

        // Configure auto-flipping every 3 seconds
        vfTodayMatches.setFlipInterval(3000);
        vfTodayMatches.setAutoStart(true);
        vfTodayMatches.startFlipping();

        // Enable horizontal swipe detection on ViewFlipper
        vfTodayMatches.setOnTouchListener(new View.OnTouchListener() {
            private float downX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        return true;

                    case MotionEvent.ACTION_UP:
                        float upX = event.getX();
                        float deltaX = upX - downX;
                        // Swipe threshold of 60 pixels
                        if (deltaX < -60) {
                            // Swiped Left -> Advance to next match card
                            vfTodayMatches.showNext();
                            return true;
                        } else if (deltaX > 60) {
                            // Swiped Right -> Return to previous match card
                            vfTodayMatches.showPrevious();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Binds explicit Intents to all dashboard navigation buttons.
     */
    private void setupNavigationButtons() {
        // 1. Fixtures
        btnFixtures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FixturesActivity.class);
                startActivity(intent);
            }
        });

        // 2. Results
        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                startActivity(intent);
            }
        });

        // 3. Upcoming Matches
        btnUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpcomingActivity.class);
                startActivity(intent);
            }
        });

        // 4. Points Table
        btnPointsTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PointsTableActivity.class);
                startActivity(intent);
            }
        });

        // 5. Rules & Discipline
        btnRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RulesActivity.class);
                startActivity(intent);
            }
        });

        // 6. About Flaviums (WebView)
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        // 7. Admin Login
        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
