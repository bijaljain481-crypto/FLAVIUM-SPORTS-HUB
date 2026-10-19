package com.flaviums.sportshub.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.flaviums.sportshub.R;
import com.flaviums.sportshub.database.DatabaseHelper;
import com.flaviums.sportshub.models.Sport;

import java.util.List;

/**
 * RulesActivity displays:
 * 1. General Discipline Guidelines (conduct rules, attendance, referee respect)
 * 2. Sport-wise Team Rules (team size, format, boys/girls restriction, branch entry limits)
 * Both sections are constructed dynamically in Java using StringBuilder and String resources.
 */
public class RulesActivity extends AppCompatActivity {

    private TextView tvDisciplineRules;
    private LinearLayout llSportRulesContainer;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        tvDisciplineRules = findViewById(R.id.tvDisciplineRules);
        llSportRulesContainer = findViewById(R.id.llSportRulesContainer);
        databaseHelper = new DatabaseHelper(this);

        buildDisciplineSection();
        buildSportRulesSection();
    }

    /**
     * Builds Section A: Discipline Guidelines using StringBuilder and String resources.
     */
    private void buildDisciplineSection() {
        int[] ruleResIds = {
                R.string.discipline_rule_1,
                R.string.discipline_rule_2,
                R.string.discipline_rule_3,
                R.string.discipline_rule_4,
                R.string.discipline_rule_5,
                R.string.discipline_rule_6,
                R.string.discipline_rule_7,
                R.string.discipline_rule_8
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruleResIds.length; i++) {
            sb.append(i + 1).append(". ")
              .append(getString(ruleResIds[i]))
              .append("\n\n");
        }

        tvDisciplineRules.setText(sb.toString().trim());
    }

    /**
     * Builds Section B: Sport-wise Team Rules using StringBuilder and Sport data.
     */
    private void buildSportRulesSection() {
        llSportRulesContainer.removeAllViews();

        List<Sport> sportsList = databaseHelper.getAllSports();
        float density = getResources().getDisplayMetrics().density;
        int p8 = (int) (8 * density);
        int p12 = (int) (12 * density);

        for (int i = 0; i < sportsList.size(); i++) {
            Sport sport = sportsList.get(i);

            // Container for each sport item
            LinearLayout sportBox = new LinearLayout(this);
            sportBox.setOrientation(LinearLayout.VERTICAL);
            sportBox.setPadding(0, 0, 0, p12);

            // Sport Title
            TextView tvTitle = new TextView(this);
            StringBuilder titleBuilder = new StringBuilder();
            titleBuilder.append(i + 1).append(". ").append(sport.getName());
            tvTitle.setText(titleBuilder.toString());
            tvTitle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            tvTitle.setTextSize(15);
            tvTitle.setTypeface(null, Typeface.BOLD);
            sportBox.addView(tvTitle);

            // Sport Metadata & Details
            TextView tvDetails = new TextView(this);
            StringBuilder detailsBuilder = new StringBuilder();
            detailsBuilder.append("• Category: ").append(sport.getCategory())
                          .append("   • Division: ").append(sport.getGender()).append("\n")
                          .append("• Regulations: ").append(sport.getTeamSizeRule()).append("\n")
                          .append("• Branch Entry: ")
                          .append(sport.isOneTeamPerBranchOnly()
                                  ? "Strictly ONE team per branch (combined 1st/2nd/3rd Year squad)"
                                  : "Branch MAY enter multiple teams/participants");

            tvDetails.setText(detailsBuilder.toString());
            tvDetails.setTextColor(getResources().getColor(R.color.textSecondary));
            tvDetails.setTextSize(13);
            tvDetails.setLineSpacing(3, 1.1f);
            sportBox.addView(tvDetails);

            // Divider between items (except last)
            if (i < sportsList.size() - 1) {
                android.view.View divider = new android.view.View(this);
                LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                );
                divParams.setMargins(0, p8, 0, 0);
                divider.setLayoutParams(divParams);
                divider.setBackgroundColor(getResources().getColor(R.color.dividerColor));
                sportBox.addView(divider);
            }

            llSportRulesContainer.addView(sportBox);
        }
    }
}
