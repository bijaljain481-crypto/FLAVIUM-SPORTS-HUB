package com.flaviums.sportshub.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaviums.sportshub.R;
import com.flaviums.sportshub.models.Match;

/**
 * Helper class to construct programmatic LinearLayout rows for matches
 * without using RecyclerView, CardView, or custom adapter classes.
 * Dynamically builds plain LinearLayout rows with TextViews as required.
 */
public class MatchViewHelper {

    /**
     * Builds a single match row as a vertical LinearLayout containing sport name,
     * status badge (green for Completed, orange for Upcoming), teams, date/time/venue,
     * and optional winner details.
     *
     * @param context   Android Context
     * @param match     Match model object
     * @param isResultsScreen If true, emphasizes winner and 1st/2nd/3rd place format
     * @return Fully configured LinearLayout row ready for addView()
     */
    public static LinearLayout buildMatchRow(final Context context, final Match match, boolean isResultsScreen) {
        // Density factor for dp-to-px conversions
        float density = context.getResources().getDisplayMetrics().density;
        int p12 = (int) (12 * density);
        int p14 = (int) (14 * density);
        int p8 = (int) (8 * density);
        int p6 = (int) (6 * density);
        int p4 = (int) (4 * density);

        // 1. Root row container
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setBackgroundResource(R.drawable.row_background);
        row.setPadding(p14, p12, p14, p12);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, 0, 0, p8);
        row.setLayoutParams(rowParams);

        // 2. Top Header (Sport Name + Status Badge)
        LinearLayout topBar = new LinearLayout(context);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Sport Name TextView
        TextView tvSport = new TextView(context);
        tvSport.setText(match.getSportName());
        tvSport.setTextColor(context.getColor(R.color.colorPrimary));
        tvSport.setTextSize(14);
        tvSport.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams sportParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        tvSport.setLayoutParams(sportParams);
        topBar.addView(tvSport);

        // Status Badge TextView (Green for Completed, Orange for Upcoming)
        TextView tvStatus = new TextView(context);
        if ("Completed".equalsIgnoreCase(match.getStatus())) {
            tvStatus.setText("COMPLETED");
            tvStatus.setBackgroundResource(R.drawable.badge_completed);
        } else {
            tvStatus.setText("UPCOMING");
            tvStatus.setBackgroundResource(R.drawable.badge_upcoming);
        }
        tvStatus.setTextColor(Color.WHITE);
        tvStatus.setTextSize(11);
        tvStatus.setTypeface(null, Typeface.BOLD);
        tvStatus.setGravity(Gravity.CENTER);
        topBar.addView(tvStatus);

        row.addView(topBar);

        // 3. Middle Match Headline (Teams or Individual Podiums)
        TextView tvTeams = new TextView(context);
        boolean isIndividual = "Swimming".equalsIgnoreCase(match.getSportName())
                || "Athletics".equalsIgnoreCase(match.getSportName());

        if (isResultsScreen && isIndividual && "Completed".equalsIgnoreCase(match.getStatus())) {
            // Special format for Swimming / Athletics on Results screen: 1st, 2nd, 3rd place
            StringBuilder podium = new StringBuilder();
            podium.append("🥇 1st: ").append(match.getWinner());
            if (match.getSecondPlace() != null && !match.getSecondPlace().isEmpty()) {
                podium.append("  |  🥈 2nd: ").append(match.getSecondPlace());
            }
            if (match.getThirdPlace() != null && !match.getThirdPlace().isEmpty()) {
                podium.append("  |  🥉 3rd: ").append(match.getThirdPlace());
            }
            tvTeams.setText(podium.toString());
            tvTeams.setTextColor(context.getColor(R.color.colorPrimaryDark));
        } else {
            // Standard Team A vs Team B format
            tvTeams.setText(match.getTeamA() + "  vs  " + match.getTeamB());
            tvTeams.setTextColor(context.getColor(R.color.textPrimary));
        }

        tvTeams.setTextSize(16);
        tvTeams.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams teamsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        teamsParams.setMargins(0, p6, 0, 0);
        tvTeams.setLayoutParams(teamsParams);
        row.addView(tvTeams);

        // 4. Result Note / Winner (if Completed and not individual event)
        if ("Completed".equalsIgnoreCase(match.getStatus()) && match.getWinner() != null && !match.getWinner().isEmpty()) {
            TextView tvWinner = new TextView(context);
            StringBuilder winText = new StringBuilder();
            winText.append("🏆 Winner: ").append(match.getWinner());
            if (match.getResultNote() != null && !match.getResultNote().isEmpty()) {
                winText.append(" (").append(match.getResultNote()).append(")");
            }
            tvWinner.setText(winText.toString());
            tvWinner.setTextColor(context.getColor(R.color.statusCompleted));
            tvWinner.setTextSize(13);
            tvWinner.setTypeface(null, Typeface.BOLD);

            LinearLayout.LayoutParams winParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            winParams.setMargins(0, p4, 0, 0);
            tvWinner.setLayoutParams(winParams);
            row.addView(tvWinner);
        }

        // 5. Date, Time, Venue Details
        TextView tvMeta = new TextView(context);
        tvMeta.setText("📅 " + match.getDate() + "   ⏰ " + match.getTime() + "   📍 " + match.getVenue());
        tvMeta.setTextColor(context.getColor(R.color.textSecondary));
        tvMeta.setTextSize(12);

        LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        metaParams.setMargins(0, p6, 0, 0);
        tvMeta.setLayoutParams(metaParams);
        row.addView(tvMeta);

        // 6. Explicit Intent on Click to launch MatchDetailActivity with match id
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MatchDetailActivity.class);
                intent.putExtra("MATCH_ID", match.getId());
                context.startActivity(intent);
            }
        });

        return row;
    }
}
