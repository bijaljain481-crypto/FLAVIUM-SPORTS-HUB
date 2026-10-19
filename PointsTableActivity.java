package com.flaviums.sportshub.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;
import com.flaviums.sportshub.database.DatabaseHelper;
import com.flaviums.sportshub.models.Branch;
import com.flaviums.sportshub.models.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PointsTableActivity calculates and displays inter-branch standings.
 * Features:
 * - Calculated strictly from completed matches in one-team-per-branch sports:
 *   Football, Cricket, and Basketball
 * - Scoring: 3 points for a win, 1 point for a draw
 * - Programmatic table rows built with LinearLayout and TextViews via addView() in a for-loop
 * - Row texts constructed using StringBuilder as required
 */
public class PointsTableActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout llPointsTableRows;

    /**
     * Inner helper class to track branch stats.
     */
    private static class BranchScore {
        String name;
        int played;
        int wins;
        int draws;
        int points;

        BranchScore(String name) {
            this.name = name;
            this.played = 0;
            this.wins = 0;
            this.draws = 0;
            this.points = 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_table);

        llPointsTableRows = findViewById(R.id.llPointsTableRows);
        databaseHelper = new DatabaseHelper(this);

        calculateAndRenderPointsTable();
    }

    /**
     * Calculates points from qualifying matches and renders table rows using StringBuilder.
     */
    private void calculateAndRenderPointsTable() {
        llPointsTableRows.removeAllViews();

        // 1. Initialize stats for all 6 NMIMS MPSTME branches
        List<Branch> branches = databaseHelper.getAllBranches();
        Map<String, BranchScore> scoreMap = new HashMap<>();
        List<BranchScore> scoreList = new ArrayList<>();

        for (Branch b : branches) {
            BranchScore bs = new BranchScore(b.getName());
            scoreMap.put(b.getName(), bs);
            scoreList.add(bs);
        }

        // 2. Fetch completed matches and filter for Football, Cricket, Basketball
        List<Match> completedMatches = databaseHelper.getCompletedMatches();

        for (Match m : completedMatches) {
            String sport = m.getSportName();
            if (sport == null) continue;

            boolean isQualifyingSport = sport.contains("Football")
                    || sport.contains("Cricket")
                    || sport.contains("Basketball");

            if (!isQualifyingSport) continue;

            String teamA = m.getTeamA();
            String teamB = m.getTeamB();
            String winner = m.getWinner();
            String note = m.getResultNote();

            BranchScore bsA = scoreMap.get(teamA);
            BranchScore bsB = scoreMap.get(teamB);

            if (bsA != null) bsA.played++;
            if (bsB != null) bsB.played++;

            boolean isDraw = (winner != null && winner.equalsIgnoreCase("Draw"))
                    || (note != null && note.toLowerCase().contains("draw"));

            if (isDraw) {
                if (bsA != null) {
                    bsA.draws++;
                    bsA.points += 1;
                }
                if (bsB != null) {
                    bsB.draws++;
                    bsB.points += 1;
                }
            } else if (winner != null) {
                if (bsA != null && winner.equalsIgnoreCase(teamA)) {
                    bsA.wins++;
                    bsA.points += 3;
                } else if (bsB != null && winner.equalsIgnoreCase(teamB)) {
                    bsB.wins++;
                    bsB.points += 3;
                }
            }
        }

        // 3. Sort branches descending by points, then wins
        Collections.sort(scoreList, new Comparator<BranchScore>() {
            @Override
            public int compare(BranchScore o1, BranchScore o2) {
                if (o2.points != o1.points) {
                    return Integer.compare(o2.points, o1.points);
                }
                return Integer.compare(o2.wins, o1.wins);
            }
        });

        // 4. Build and add LinearLayout rows in Java loop using addView() and StringBuilder
        float density = getResources().getDisplayMetrics().density;
        int p8 = (int) (8 * density);
        int p10 = (int) (10 * density);

        for (int i = 0; i < scoreList.size(); i++) {
            BranchScore bs = scoreList.get(i);
            int rank = i + 1;

            // Row container
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, p10, 0, p10);

            // Alternating subtle background color for rows
            if (i % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#F8FAFC"));
            } else {
                row.setBackgroundColor(Color.WHITE);
            }

            // Construct row strings using StringBuilder
            StringBuilder rankBuilder = new StringBuilder();
            rankBuilder.append(rank);

            StringBuilder branchBuilder = new StringBuilder();
            branchBuilder.append(bs.name);

            StringBuilder playedBuilder = new StringBuilder();
            playedBuilder.append(bs.played);

            StringBuilder winsBuilder = new StringBuilder();
            winsBuilder.append(bs.wins);

            StringBuilder ptsBuilder = new StringBuilder();
            ptsBuilder.append(bs.points);

            // Rank Column
            TextView tvRank = new TextView(this);
            tvRank.setLayoutParams(new LinearLayout.LayoutParams((int) (40 * density), LinearLayout.LayoutParams.WRAP_CONTENT));
            tvRank.setText(rankBuilder.toString());
            tvRank.setGravity(Gravity.CENTER);
            tvRank.setTextColor(getResources().getColor(R.color.textSecondary));
            tvRank.setTextSize(13);
            tvRank.setTypeface(null, Typeface.BOLD);
            row.addView(tvRank);

            // Branch Name Column
            TextView tvBranch = new TextView(this);
            LinearLayout.LayoutParams branchParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            tvBranch.setLayoutParams(branchParams);
            tvBranch.setText(branchBuilder.toString());
            tvBranch.setTextColor(getResources().getColor(R.color.textPrimary));
            tvBranch.setTextSize(14);
            tvBranch.setTypeface(null, Typeface.BOLD);
            row.addView(tvBranch);

            // Played Column
            TextView tvPlayed = new TextView(this);
            tvPlayed.setLayoutParams(new LinearLayout.LayoutParams((int) (35 * density), LinearLayout.LayoutParams.WRAP_CONTENT));
            tvPlayed.setText(playedBuilder.toString());
            tvPlayed.setGravity(Gravity.CENTER);
            tvPlayed.setTextColor(getResources().getColor(R.color.textSecondary));
            tvPlayed.setTextSize(13);
            row.addView(tvPlayed);

            // Wins Column
            TextView tvWins = new TextView(this);
            tvWins.setLayoutParams(new LinearLayout.LayoutParams((int) (35 * density), LinearLayout.LayoutParams.WRAP_CONTENT));
            tvWins.setText(winsBuilder.toString());
            tvWins.setGravity(Gravity.CENTER);
            tvWins.setTextColor(getResources().getColor(R.color.textPrimary));
            tvWins.setTextSize(13);
            row.addView(tvWins);

            // Points Column
            TextView tvPts = new TextView(this);
            tvPts.setLayoutParams(new LinearLayout.LayoutParams((int) (45 * density), LinearLayout.LayoutParams.WRAP_CONTENT));
            tvPts.setText(ptsBuilder.toString());
            tvPts.setGravity(Gravity.CENTER);
            tvPts.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvPts.setTextSize(15);
            tvPts.setTypeface(null, Typeface.BOLD);
            row.addView(tvPts);

            // Add row to table container
            llPointsTableRows.addView(row);
        }
    }
}
