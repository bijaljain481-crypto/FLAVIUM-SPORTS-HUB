package com.flaviums.sportshub.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;
import com.flaviums.sportshub.database.DatabaseHelper;
import com.flaviums.sportshub.models.Match;
import com.flaviums.sportshub.models.Sport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * FixturesActivity displays all scheduled inter-branch matches.
 * Features:
 * - LinearLayout rows inside a ScrollView created dynamically with addView() in a for-loop
 * - Top SearchView for live query filtering by sport or team name
 * - Spinner to filter by sport discipline
 * - DatePickerDialog to filter by date
 * - Empty state feedback when no matches match current filters
 * - Navigation to MatchDetailActivity with match id on row click
 */
public class FixturesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private List<Match> allMatchesList;

    private SearchView searchView;
    private Spinner spinnerSportFilter;
    private Button btnPickDate;
    private Button btnClearDate;
    private TextView tvFilterSummary;
    private TextView tvNoMatches;
    private LinearLayout llMatchesContainer;

    // Active filter parameters
    private String selectedSport = "All Sports";
    private String selectedDate = "";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);

        // Bind layout views
        searchView = findViewById(R.id.searchView);
        spinnerSportFilter = findViewById(R.id.spinnerSportFilter);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnClearDate = findViewById(R.id.btnClearDate);
        tvFilterSummary = findViewById(R.id.tvFilterSummary);
        tvNoMatches = findViewById(R.id.tvNoMatches);
        llMatchesContainer = findViewById(R.id.llMatchesContainer);

        // Initialize SQLite helper and load matches
        databaseHelper = new DatabaseHelper(this);
        allMatchesList = databaseHelper.getAllMatches();

        // Setup filter controls
        setupSportSpinner();
        setupDatePicker();
        setupSearchView();

        // Initial render of all matches
        renderFilteredMatches();
    }

    /**
     * Sets up the Spinner for filtering fixtures by sport discipline.
     */
    private void setupSportSpinner() {
        List<Sport> sports = databaseHelper.getAllSports();
        List<String> sportOptions = new ArrayList<>();
        sportOptions.add("All Sports");
        for (Sport s : sports) {
            sportOptions.add(s.getName());
        }

        // Standard framework ArrayAdapter for Spinner (no custom adapter classes)
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sportOptions
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSportFilter.setAdapter(spinnerAdapter);

        spinnerSportFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSport = sportOptions.get(position);
                renderFilteredMatches();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Sets up DatePickerDialog to filter fixtures by date.
     */
    private void setupDatePicker() {
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        FixturesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int y, int m, int d) {
                                // Format date as YYYY-MM-DD
                                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                                btnPickDate.setText("📅 " + selectedDate);
                                btnClearDate.setVisibility(View.VISIBLE);
                                renderFilteredMatches();
                            }
                        },
                        year, month, day
                );
                dialog.show();
            }
        });

        btnClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = "";
                btnPickDate.setText("📅 Date");
                btnClearDate.setVisibility(View.GONE);
                renderFilteredMatches();
            }
        });
    }

    /**
     * Sets up SearchView for dynamic filtering as the user types.
     */
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = (query != null) ? query.trim().toLowerCase() : "";
                renderFilteredMatches();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = (newText != null) ? newText.trim().toLowerCase() : "";
                renderFilteredMatches();
                return true;
            }
        });
    }

    /**
     * Filters all matches according to sport, date, and query criteria,
     * then populates the LinearLayout inside ScrollView in a for-loop using addView().
     */
    private void renderFilteredMatches() {
        // Clear previous rows
        llMatchesContainer.removeAllViews();

        List<Match> matchingResults = new ArrayList<>();

        for (Match match : allMatchesList) {
            // Check sport filter
            if (!"All Sports".equals(selectedSport) && !match.getSportName().equalsIgnoreCase(selectedSport)) {
                continue;
            }

            // Check date filter
            if (!selectedDate.isEmpty() && !match.getDate().equals(selectedDate)) {
                continue;
            }

            // Check search text filter (sport, teamA, teamB)
            if (!searchQuery.isEmpty()) {
                String searchable = (match.getSportName() + " " + match.getTeamA() + " " + match.getTeamB() + " " + match.getVenue()).toLowerCase();
                if (!searchable.contains(searchQuery)) {
                    continue;
                }
            }

            matchingResults.add(match);
        }

        // Update summary label using StringBuilder
        StringBuilder summary = new StringBuilder();
        summary.append("Showing ").append(matchingResults.size()).append(" fixtures");
        if (!"All Sports".equals(selectedSport)) {
            summary.append(" • ").append(selectedSport);
        }
        if (!selectedDate.isEmpty()) {
            summary.append(" • ").append(selectedDate);
        }
        tvFilterSummary.setText(summary.toString());

        // Handle empty list feedback
        if (matchingResults.isEmpty()) {
            tvNoMatches.setVisibility(View.VISIBLE);
        } else {
            tvNoMatches.setVisibility(View.GONE);

            // Programmatically add LinearLayout rows in a for-loop via addView()
            for (Match match : matchingResults) {
                LinearLayout row = MatchViewHelper.buildMatchRow(this, match, false);
                llMatchesContainer.addView(row);
            }
        }
    }
}
