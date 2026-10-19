package com.flaviums.sportshub.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;
import com.flaviums.sportshub.database.DatabaseHelper;
import com.flaviums.sportshub.models.Match;

import java.util.List;

/**
 * UpcomingActivity displays only upcoming matches,
 * sorted chronologically by date and time (nearest first).
 * Constructed programmatically using LinearLayout rows inside ScrollView with addView().
 */
public class UpcomingActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout llUpcomingContainer;
    private TextView tvNoUpcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        llUpcomingContainer = findViewById(R.id.llUpcomingContainer);
        tvNoUpcoming = findViewById(R.id.tvNoUpcoming);

        databaseHelper = new DatabaseHelper(this);
        loadUpcomingMatches();
    }

    /**
     * Loads upcoming matches ordered by date/time, adding each row to the container.
     */
    private void loadUpcomingMatches() {
        llUpcomingContainer.removeAllViews();

        List<Match> upcomingList = databaseHelper.getUpcomingMatches();

        if (upcomingList == null || upcomingList.isEmpty()) {
            tvNoUpcoming.setVisibility(View.VISIBLE);
        } else {
            tvNoUpcoming.setVisibility(View.GONE);

            // Populate rows in Java loop using addView()
            for (Match match : upcomingList) {
                LinearLayout row = MatchViewHelper.buildMatchRow(this, match, false);
                llUpcomingContainer.addView(row);
            }
        }
    }
}
