package com.flaviums.sportshub.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;
import com.flaviums.sportshub.database.DatabaseHelper;
import com.flaviums.sportshub.models.Match;

/**
 * MatchDetailActivity displays complete details for a specific match fixture.
 * Features:
 * - Retrieves match ID via explicit Intent extra
 * - Loads match record from SQLite via DatabaseHelper
 * - Displays all fields including sport name, status, teams, date, time, venue, and results
 * - Implicit Intent button to view the venue on Google Maps
 */
public class MatchDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Match match;

    private TextView tvDetailSport;
    private TextView tvDetailStatusBadge;
    private TextView tvDetailTeams;
    private TextView tvDetailDateTime;
    private TextView tvDetailVenue;
    private Button btnViewVenueOnMap;

    private LinearLayout llResultCard;
    private TextView tvDetailWinner;
    private TextView tvDetailResultNote;
    private TextView tvDetailSecondPlace;
    private TextView tvDetailThirdPlace;
    private TextView tvDetailMatchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        // Bind layout views
        tvDetailSport = findViewById(R.id.tvDetailSport);
        tvDetailStatusBadge = findViewById(R.id.tvDetailStatusBadge);
        tvDetailTeams = findViewById(R.id.tvDetailTeams);
        tvDetailDateTime = findViewById(R.id.tvDetailDateTime);
        tvDetailVenue = findViewById(R.id.tvDetailVenue);
        btnViewVenueOnMap = findViewById(R.id.btnViewVenueOnMap);

        llResultCard = findViewById(R.id.llResultCard);
        tvDetailWinner = findViewById(R.id.tvDetailWinner);
        tvDetailResultNote = findViewById(R.id.tvDetailResultNote);
        tvDetailSecondPlace = findViewById(R.id.tvDetailSecondPlace);
        tvDetailThirdPlace = findViewById(R.id.tvDetailThirdPlace);
        tvDetailMatchId = findViewById(R.id.tvDetailMatchId);

        // Retrieve match ID from Intent
        int matchId = getIntent().getIntExtra("MATCH_ID", -1);
        if (matchId == -1) {
            Toast.makeText(this, "Error: Invalid match ID specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseHelper = new DatabaseHelper(this);
        match = databaseHelper.getMatchById(matchId);

        if (match == null) {
            Toast.makeText(this, "Match record not found in database", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate match data
        populateMatchDetails();

        // Setup Implicit Intent for Google Maps venue search
        setupGoogleMapsButton();
    }

    /**
     * Binds all fields of the Match object to the respective UI TextViews.
     */
    private void populateMatchDetails() {
        tvDetailSport.setText(match.getSportName());
        tvDetailMatchId.setText("Fixture Reference ID: #" + match.getId());

        // Status badge
        if ("Completed".equalsIgnoreCase(match.getStatus())) {
            tvDetailStatusBadge.setText("COMPLETED");
            tvDetailStatusBadge.setBackgroundResource(R.drawable.badge_completed);
        } else {
            tvDetailStatusBadge.setText("UPCOMING");
            tvDetailStatusBadge.setBackgroundResource(R.drawable.badge_upcoming);
        }

        // Teams display
        boolean isIndividual = "Swimming".equalsIgnoreCase(match.getSportName())
                || "Athletics".equalsIgnoreCase(match.getSportName());

        if (isIndividual) {
            tvDetailTeams.setText("Solo / Relay Event: " + match.getTeamA() + " & " + match.getTeamB());
        } else {
            tvDetailTeams.setText(match.getTeamA() + "  vs  " + match.getTeamB());
        }

        // Date, Time, Venue
        tvDetailDateTime.setText("📅 Date: " + match.getDate() + "   •   ⏰ Time: " + match.getTime());
        tvDetailVenue.setText("📍 Venue: " + match.getVenue());

        // Result Card handling
        if ("Completed".equalsIgnoreCase(match.getStatus())) {
            llResultCard.setVisibility(View.VISIBLE);

            if (match.getWinner() != null && !match.getWinner().isEmpty()) {
                tvDetailWinner.setText("🏆 Winner: " + match.getWinner());
            } else {
                tvDetailWinner.setText("🏆 Winner: Match Concluded");
            }

            if (match.getResultNote() != null && !match.getResultNote().isEmpty()) {
                tvDetailResultNote.setText("Score / Notes: " + match.getResultNote());
                tvDetailResultNote.setVisibility(View.VISIBLE);
            } else {
                tvDetailResultNote.setVisibility(View.GONE);
            }

            // 2nd and 3rd place podium (for Swimming / Athletics)
            if (match.getSecondPlace() != null && !match.getSecondPlace().isEmpty()) {
                tvDetailSecondPlace.setText("🥈 2nd Place: " + match.getSecondPlace());
                tvDetailSecondPlace.setVisibility(View.VISIBLE);
            } else {
                tvDetailSecondPlace.setVisibility(View.GONE);
            }

            if (match.getThirdPlace() != null && !match.getThirdPlace().isEmpty()) {
                tvDetailThirdPlace.setText("🥉 3rd Place: " + match.getThirdPlace());
                tvDetailThirdPlace.setVisibility(View.VISIBLE);
            } else {
                tvDetailThirdPlace.setVisibility(View.GONE);
            }
        } else {
            // Match is still upcoming
            llResultCard.setVisibility(View.GONE);
        }
    }

    /**
     * Sets up implicit Intent to open Google Maps searching for the match venue.
     */
    private void setupGoogleMapsButton() {
        btnViewVenueOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String venueQuery = match.getVenue() + ", NMIMS MPSTME Mumbai";

                // Implicit Intent using geo URI scheme
                Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(venueQuery));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);

                // Check if Google Maps or compatible map handler exists
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback using browser URL to Google Maps web search
                    Uri webMapUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(venueQuery));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webMapUri);
                    startActivity(webIntent);
                }
            }
        });
    }
}
