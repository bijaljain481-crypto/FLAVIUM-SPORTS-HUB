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
 * ResultsActivity displays only completed matches.
 * Features:
 * - LinearLayout rows inside a ScrollView created dynamically with addView() in a for-loop
 * - Shows winner and result note for team matches
 * - For individual sports (Swimming, Athletics), renders 1st, 2nd, and 3rd place podium standings
 * - Row click launches MatchDetailActivity with the match id
 */
public class ResultsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout llResultsContainer;
    private TextView tvNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        llResultsContainer = findViewById(R.id.llResultsContainer);
        tvNoResults = findViewById(R.id.tvNoResults);

        databaseHelper = new DatabaseHelper(this);
        loadCompletedResults();
    }

    /**
     * Loads completed matches from the SQLite database and adds LinearLayout rows via addView().
     */
    private void loadCompletedResults() {
        llResultsContainer.removeAllViews();

        List<Match> completedList = databaseHelper.getCompletedMatches();

        if (completedList == null || completedList.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            tvNoResults.setVisibility(View.GONE);

            // Populate rows in Java loop using addView()
            for (Match match : completedList) {
                // Pass isResultsScreen = true to trigger Swimming/Athletics podium formatting
                LinearLayout row = MatchViewHelper.buildMatchRow(this, match, true);
                llResultsContainer.addView(row);
            }
        }
    }
}
