package com.flaviums.sportshub.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.flaviums.sportshub.models.Branch;
import com.flaviums.sportshub.models.Match;
import com.flaviums.sportshub.models.Sport;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper manages the SQLite database for the Flaviums Sports Hub app.
 * Handles table creation, initial data seeding, and CRUD operations for:
 * - sports (14 college sports and rules)
 * - branches (6 engineering departments)
 * - matches (fixtures, schedules, and scores)
 * - admin (authentication credentials)
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database details
    private static final String DATABASE_NAME = "FlaviumsSportsHub.db";
    private static final int DATABASE_VERSION = 1;

    // --- Table Names ---
    public static final String TABLE_SPORTS = "sports";
    public static final String TABLE_BRANCHES = "branches";
    public static final String TABLE_MATCHES = "matches";
    public static final String TABLE_ADMIN = "admin";

    // --- Common Column ---
    public static final String COLUMN_ID = "id";

    // --- Columns for 'sports' table ---
    public static final String COLUMN_SPORT_NAME = "name";
    public static final String COLUMN_SPORT_CATEGORY = "category";
    public static final String COLUMN_SPORT_GENDER = "gender";
    public static final String COLUMN_SPORT_TEAM_SIZE_RULE = "team_size_rule";
    public static final String COLUMN_SPORT_ONE_TEAM_PER_BRANCH = "one_team_per_branch";

    // --- Columns for 'branches' table ---
    public static final String COLUMN_BRANCH_NAME = "name";

    // --- Columns for 'matches' table ---
    public static final String COLUMN_MATCH_SPORT_NAME = "sport_name";
    public static final String COLUMN_MATCH_TEAM_A = "team_a";
    public static final String COLUMN_MATCH_TEAM_B = "team_b";
    public static final String COLUMN_MATCH_DATE = "date";
    public static final String COLUMN_MATCH_TIME = "time";
    public static final String COLUMN_MATCH_VENUE = "venue";
    public static final String COLUMN_MATCH_STATUS = "status";
    public static final String COLUMN_MATCH_WINNER = "winner";
    public static final String COLUMN_MATCH_RESULT_NOTE = "result_note";
    public static final String COLUMN_MATCH_SECOND_PLACE = "second_place";
    public static final String COLUMN_MATCH_THIRD_PLACE = "third_place";

    // --- Columns for 'admin' table ---
    public static final String COLUMN_ADMIN_USERNAME = "username";
    public static final String COLUMN_ADMIN_PASSWORD = "password";

    private final Context context;

    /**
     * Constructor for DatabaseHelper.
     *
     * @param context Application context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create sports table
        String createSportsTable = "CREATE TABLE " + TABLE_SPORTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SPORT_NAME + " TEXT NOT NULL, "
                + COLUMN_SPORT_CATEGORY + " TEXT, "
                + COLUMN_SPORT_GENDER + " TEXT, "
                + COLUMN_SPORT_TEAM_SIZE_RULE + " TEXT, "
                + COLUMN_SPORT_ONE_TEAM_PER_BRANCH + " INTEGER DEFAULT 0"
                + ");";

        // Create branches table
        String createBranchesTable = "CREATE TABLE " + TABLE_BRANCHES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BRANCH_NAME + " TEXT UNIQUE NOT NULL"
                + ");";

        // Create matches table
        String createMatchesTable = "CREATE TABLE " + TABLE_MATCHES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MATCH_SPORT_NAME + " TEXT NOT NULL, "
                + COLUMN_MATCH_TEAM_A + " TEXT NOT NULL, "
                + COLUMN_MATCH_TEAM_B + " TEXT NOT NULL, "
                + COLUMN_MATCH_DATE + " TEXT NOT NULL, "
                + COLUMN_MATCH_TIME + " TEXT NOT NULL, "
                + COLUMN_MATCH_VENUE + " TEXT NOT NULL, "
                + COLUMN_MATCH_STATUS + " TEXT NOT NULL, "
                + COLUMN_MATCH_WINNER + " TEXT, "
                + COLUMN_MATCH_RESULT_NOTE + " TEXT, "
                + COLUMN_MATCH_SECOND_PLACE + " TEXT, "
                + COLUMN_MATCH_THIRD_PLACE + " TEXT"
                + ");";

        // Create admin table
        String createAdminTable = "CREATE TABLE " + TABLE_ADMIN + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ADMIN_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COLUMN_ADMIN_PASSWORD + " TEXT NOT NULL"
                + ");";

        // Execute table creation statements
        db.execSQL(createSportsTable);
        db.execSQL(createBranchesTable);
        db.execSQL(createMatchesTable);
        db.execSQL(createAdminTable);

        Log.d(TAG, "All tables created successfully.");

        // Seed initial sample data on first run
        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables on schema upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPORTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        onCreate(db);
    }

    /**
     * Seeds initial database entries:
     * - 14 Sports with their official rules
     * - 6 NMIMS MPSTME Engineering Branches
     * - Default Admin credentials (admin / admin123)
     * - 13 Sample Matches spread across sports with Upcoming and Completed mix
     */
    private void seedInitialData(SQLiteDatabase db) {
        Log.d(TAG, "Seeding initial data...");

        // 1. Seed Branches
        String[] branchNames = {
                "MBA Tech CE",
                "BTech CE",
                "CE",
                "IT",
                "AIML",
                "CSDS"
        };
        for (String branch : branchNames) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_BRANCH_NAME, branch);
            db.insert(TABLE_BRANCHES, null, cv);
        }

        // 2. Seed Default Admin
        ContentValues adminCv = new ContentValues();
        adminCv.put(COLUMN_ADMIN_USERNAME, "admin");
        adminCv.put(COLUMN_ADMIN_PASSWORD, "admin123");
        db.insert(TABLE_ADMIN, null, adminCv);

        // 3. Seed 14 Sports with rules and squad guidelines
        Sport[] sportsList = {
                new Sport("Cricket (Boys)", "Outdoor", "Boys", "Standard squad (11 players), ONE team per branch only.", true),
                new Sport("Box Cricket (Girls)", "Outdoor", "Girls", "7 players per team, 5 overs per side, ONE team per branch only.", true),
                new Sport("Basketball (Boys)", "Outdoor", "Boys", "11 players per team, standard rules, ONE team per branch only. Boys and Girls are always separate — never mixed.", true),
                new Sport("Basketball (Girls)", "Outdoor", "Girls", "11 players per team, standard rules, ONE team per branch only.", true),
                new Sport("Football (Boys only)", "Outdoor", "Boys", "Standard rules, ONE team per branch only.", true),
                new Sport("Badminton", "Indoor", "Mixed", "Team of minimum 3, maximum 4 players, standard rules, a branch MAY enter multiple teams.", false),
                new Sport("Lawn Tennis", "Outdoor", "Mixed", "Team of minimum 3, maximum 4 players, standard rules, a branch MAY enter multiple teams.", false),
                new Sport("Table Tennis", "Indoor", "Mixed", "Indoor, any number of participants, standard rules.", false),
                new Sport("Chess", "Indoor", "Mixed", "Indoor, any number of participants, standard rules.", false),
                new Sport("Carrom", "Indoor", "Mixed", "Indoor, any number of participants, standard rules.", false),
                new Sport("8-Ball Pool", "Indoor", "Boys", "Boys only, standard rules.", false),
                new Sport("Swimming", "Outdoor", "Mixed", "Individual solo events plus a 4-member relay per branch, standard rules. Uses secondPlace/thirdPlace on Match.", false),
                new Sport("Athletics", "Outdoor", "Mixed", "Individual solo events, standard rules. Uses secondPlace/thirdPlace on Match.", false),
                new Sport("Tug of War", "Outdoor", "Mixed", "9-member team (7 playing + 2 substitutes), best of 3 rounds, standard rules.", true)
        };

        for (Sport s : sportsList) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_SPORT_NAME, s.getName());
            cv.put(COLUMN_SPORT_CATEGORY, s.getCategory());
            cv.put(COLUMN_SPORT_GENDER, s.getGender());
            cv.put(COLUMN_SPORT_TEAM_SIZE_RULE, s.getTeamSizeRule());
            cv.put(COLUMN_SPORT_ONE_TEAM_PER_BRANCH, s.isOneTeamPerBranchOnly() ? 1 : 0);
            db.insert(TABLE_SPORTS, null, cv);
        }

        // 4. Seed At least 12 Sample Matches (14 total) across multiple sports
        Match[] sampleMatches = {
                // Match 1: Cricket (Boys) - Upcoming
                new Match("Cricket (Boys)", "IT", "AIML", "2026-10-05", "09:00 AM",
                        "Main Cricket Ground", "Upcoming", "", "", "", ""),

                // Match 2: Cricket (Boys) - Completed
                new Match("Cricket (Boys)", "BTech CE", "MBA Tech CE", "2026-10-01", "10:00 AM",
                        "Main Cricket Ground", "Completed", "BTech CE", "Won by 24 runs", "", ""),

                // Match 3: Box Cricket (Girls) - Upcoming
                new Match("Box Cricket (Girls)", "CE", "CSDS", "2026-10-06", "11:00 AM",
                        "Turf Arena", "Upcoming", "", "", "", ""),

                // Match 4: Box Cricket (Girls) - Completed
                new Match("Box Cricket (Girls)", "IT", "AIML", "2026-10-02", "10:30 AM",
                        "Turf Arena", "Completed", "IT", "Chased 42 runs in 4.2 overs", "", ""),

                // Match 5: Football (Boys only) - Completed
                new Match("Football (Boys only)", "IT", "MBA Tech CE", "2026-10-02", "04:00 PM",
                        "Football Turf", "Completed", "IT", "Won 3-1 on penalty shootouts", "", ""),

                // Match 6: Football (Boys only) - Upcoming
                new Match("Football (Boys only)", "AIML", "BTech CE", "2026-10-07", "03:30 PM",
                        "Football Turf", "Upcoming", "", "", "", ""),

                // Match 7: Basketball (Boys) - Completed
                new Match("Basketball (Boys)", "CSDS", "IT", "2026-10-03", "02:00 PM",
                        "Basketball Court", "Completed", "IT", "Final score 48 - 42", "", ""),

                // Match 8: Basketball (Girls) - Upcoming
                new Match("Basketball (Girls)", "AIML", "MBA Tech CE", "2026-10-08", "01:30 PM",
                        "Basketball Court", "Upcoming", "", "", "", ""),

                // Match 9: Badminton - Completed
                new Match("Badminton", "BTech CE", "CE", "2026-10-03", "11:30 AM",
                        "Indoor Sports Complex", "Completed", "CE", "Sets 2 - 1 (21-18, 19-21, 21-17)", "", ""),

                // Match 10: Table Tennis - Upcoming
                new Match("Table Tennis", "IT", "CSDS", "2026-10-09", "10:30 AM",
                        "TT Hall", "Upcoming", "", "", "", ""),

                // Match 11: Chess - Completed
                new Match("Chess", "AIML", "MBA Tech CE", "2026-10-04", "11:00 AM",
                        "Recreation Room", "Completed", "AIML", "Checkmate in 34 moves", "", ""),

                // Match 12: Swimming - Completed (Individual event with 2nd and 3rd place)
                new Match("Swimming", "BTech CE", "IT", "2026-10-04", "03:00 PM",
                        "Olympic Pool", "Completed", "BTech CE", "50m Freestyle Final (24.8s)", "IT", "AIML"),

                // Match 13: Athletics - Upcoming (Individual event)
                new Match("Athletics", "MBA Tech CE", "CE", "2026-10-10", "08:30 AM",
                        "University Track", "Upcoming", "", "", "", ""),

                // Match 14: Tug of War - Upcoming
                new Match("Tug of War", "CE", "BTech CE", "2026-10-11", "04:30 PM",
                        "Sports Ground 2", "Upcoming", "", "", "", "")
        };

        for (Match m : sampleMatches) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_MATCH_SPORT_NAME, m.getSportName());
            cv.put(COLUMN_MATCH_TEAM_A, m.getTeamA());
            cv.put(COLUMN_MATCH_TEAM_B, m.getTeamB());
            cv.put(COLUMN_MATCH_DATE, m.getDate());
            cv.put(COLUMN_MATCH_TIME, m.getTime());
            cv.put(COLUMN_MATCH_VENUE, m.getVenue());
            cv.put(COLUMN_MATCH_STATUS, m.getStatus());
            cv.put(COLUMN_MATCH_WINNER, m.getWinner());
            cv.put(COLUMN_MATCH_RESULT_NOTE, m.getResultNote());
            cv.put(COLUMN_MATCH_SECOND_PLACE, m.getSecondPlace());
            cv.put(COLUMN_MATCH_THIRD_PLACE, m.getThirdPlace());
            db.insert(TABLE_MATCHES, null, cv);
        }

        Log.d(TAG, "Sample data seeded successfully: 6 branches, 14 sports, 14 matches, 1 admin.");
    }

    // =========================================================================
    // CRUD METHODS
    // =========================================================================

    /**
     * Adds a new match to the database.
     *
     * @param match Match object with details
     * @return Row ID of the newly inserted record, or -1 on error
     */
    public long addMatch(Match match) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MATCH_SPORT_NAME, match.getSportName());
        cv.put(COLUMN_MATCH_TEAM_A, match.getTeamA());
        cv.put(COLUMN_MATCH_TEAM_B, match.getTeamB());
        cv.put(COLUMN_MATCH_DATE, match.getDate());
        cv.put(COLUMN_MATCH_TIME, match.getTime());
        cv.put(COLUMN_MATCH_VENUE, match.getVenue());
        cv.put(COLUMN_MATCH_STATUS, match.getStatus());
        cv.put(COLUMN_MATCH_WINNER, match.getWinner());
        cv.put(COLUMN_MATCH_RESULT_NOTE, match.getResultNote());
        cv.put(COLUMN_MATCH_SECOND_PLACE, match.getSecondPlace());
        cv.put(COLUMN_MATCH_THIRD_PLACE, match.getThirdPlace());
        return db.insert(TABLE_MATCHES, null, cv);
    }

    /**
     * Updates an existing match in the database.
     *
     * @param match Match object containing updated fields and valid ID
     * @return Number of rows affected
     */
    public int updateMatch(Match match) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MATCH_SPORT_NAME, match.getSportName());
        cv.put(COLUMN_MATCH_TEAM_A, match.getTeamA());
        cv.put(COLUMN_MATCH_TEAM_B, match.getTeamB());
        cv.put(COLUMN_MATCH_DATE, match.getDate());
        cv.put(COLUMN_MATCH_TIME, match.getTime());
        cv.put(COLUMN_MATCH_VENUE, match.getVenue());
        cv.put(COLUMN_MATCH_STATUS, match.getStatus());
        cv.put(COLUMN_MATCH_WINNER, match.getWinner());
        cv.put(COLUMN_MATCH_RESULT_NOTE, match.getResultNote());
        cv.put(COLUMN_MATCH_SECOND_PLACE, match.getSecondPlace());
        cv.put(COLUMN_MATCH_THIRD_PLACE, match.getThirdPlace());
        return db.update(TABLE_MATCHES, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(match.getId())});
    }

    /**
     * Deletes a match from the database by ID.
     *
     * @param id ID of the match to delete
     * @return Number of rows deleted
     */
    public int deleteMatch(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MATCHES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Retrieves all matches ordered by date and time.
     *
     * @return List of all Match objects
     */
    public List<Match> getAllMatches() {
        return queryMatches(null, null, COLUMN_MATCH_DATE + " ASC, " + COLUMN_MATCH_TIME + " ASC");
    }

    /**
     * Retrieves all matches scheduled on a specific date.
     *
     * @param date Date string in format YYYY-MM-DD
     * @return List of Match objects on that date
     */
    public List<Match> getMatchesByDate(String date) {
        return queryMatches(COLUMN_MATCH_DATE + " = ?", new String[]{date}, COLUMN_MATCH_TIME + " ASC");
    }

    /**
     * Retrieves all matches belonging to a specific sport.
     *
     * @param sportName Name of the sport
     * @return List of Match objects for the sport
     */
    public List<Match> getMatchesBySport(String sportName) {
        return queryMatches(COLUMN_MATCH_SPORT_NAME + " = ?", new String[]{sportName}, COLUMN_MATCH_DATE + " ASC");
    }

    /**
     * Retrieves all matches with status "Upcoming".
     *
     * @return List of upcoming Match objects
     */
    public List<Match> getUpcomingMatches() {
        return queryMatches(COLUMN_MATCH_STATUS + " = ?", new String[]{"Upcoming"}, COLUMN_MATCH_DATE + " ASC");
    }

    /**
     * Retrieves all matches with status "Completed".
     *
     * @return List of completed Match objects
     */
    public List<Match> getCompletedMatches() {
        return queryMatches(COLUMN_MATCH_STATUS + " = ?", new String[]{"Completed"}, COLUMN_MATCH_DATE + " DESC");
    }

    /**
     * Retrieves a single match by its primary key ID.
     *
     * @param id ID of the match
     * @return Match object if found, or null otherwise
     */
    public Match getMatchById(int id) {
        List<Match> results = queryMatches(COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null);
        if (results != null && !results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    /**
     * Marks the final result of a match and updates its status to "Completed".
     * Supports recording winner, result note, and 2nd/3rd places for individual events.
     *
     * @param matchId     ID of the match
     * @param winner      Name of the winning branch
     * @param resultNote  Score/details description
     * @param secondPlace Runner-up (for individual sports)
     * @param thirdPlace  Second runner-up (for individual sports)
     * @return Number of rows updated
     */
    public int markResult(int matchId, String winner, String resultNote, String secondPlace, String thirdPlace) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MATCH_STATUS, "Completed");
        cv.put(COLUMN_MATCH_WINNER, winner);
        cv.put(COLUMN_MATCH_RESULT_NOTE, resultNote);
        cv.put(COLUMN_MATCH_SECOND_PLACE, secondPlace);
        cv.put(COLUMN_MATCH_THIRD_PLACE, thirdPlace);
        return db.update(TABLE_MATCHES, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(matchId)});
    }

    /**
     * Authenticates administrator login credentials.
     *
     * @param username Admin username
     * @param password Admin password
     * @return true if credentials match a record in admin table, false otherwise
     */
    public boolean checkAdminLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ADMIN,
                    new String[]{COLUMN_ID},
                    COLUMN_ADMIN_USERNAME + " = ? AND " + COLUMN_ADMIN_PASSWORD + " = ?",
                    new String[]{username, password},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Retrieves all sports from the sports table.
     *
     * @return List of Sport objects
     */
    public List<Sport> getAllSports() {
        List<Sport> sportsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SPORTS, null, null, null, null, null, COLUMN_SPORT_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int nameIdx = cursor.getColumnIndexOrThrow(COLUMN_SPORT_NAME);
                int catIdx = cursor.getColumnIndexOrThrow(COLUMN_SPORT_CATEGORY);
                int genderIdx = cursor.getColumnIndexOrThrow(COLUMN_SPORT_GENDER);
                int ruleIdx = cursor.getColumnIndexOrThrow(COLUMN_SPORT_TEAM_SIZE_RULE);
                int oneTeamIdx = cursor.getColumnIndexOrThrow(COLUMN_SPORT_ONE_TEAM_PER_BRANCH);

                do {
                    Sport sport = new Sport(
                            cursor.getInt(idIdx),
                            cursor.getString(nameIdx),
                            cursor.getString(catIdx),
                            cursor.getString(genderIdx),
                            cursor.getString(ruleIdx),
                            cursor.getInt(oneTeamIdx) == 1
                    );
                    sportsList.add(sport);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return sportsList;
    }

    /**
     * Retrieves all branches from the branches table.
     *
     * @return List of Branch objects
     */
    public List<Branch> getAllBranches() {
        List<Branch> branchList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BRANCHES, null, null, null, null, null, COLUMN_ID + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int nameIdx = cursor.getColumnIndexOrThrow(COLUMN_BRANCH_NAME);

                do {
                    Branch branch = new Branch(
                            cursor.getInt(idIdx),
                            cursor.getString(nameIdx)
                    );
                    branchList.add(branch);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return branchList;
    }

    /**
     * Internal helper method to query matches table with given criteria.
     *
     * @param selection     WHERE clause
     * @param selectionArgs WHERE parameters
     * @param orderBy       ORDER BY clause
     * @return List of matching Match objects
     */
    private List<Match> queryMatches(String selection, String[] selectionArgs, String orderBy) {
        List<Match> matches = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MATCHES, null, selection, selectionArgs, null, null, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                int idIdx = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int sportIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_SPORT_NAME);
                int teamAIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_TEAM_A);
                int teamBIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_TEAM_B);
                int dateIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_DATE);
                int timeIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_TIME);
                int venueIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_VENUE);
                int statusIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_STATUS);
                int winnerIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_WINNER);
                int resultIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_RESULT_NOTE);
                int secondIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_SECOND_PLACE);
                int thirdIdx = cursor.getColumnIndexOrThrow(COLUMN_MATCH_THIRD_PLACE);

                do {
                    Match match = new Match(
                            cursor.getInt(idIdx),
                            cursor.getString(sportIdx),
                            cursor.getString(teamAIdx),
                            cursor.getString(teamBIdx),
                            cursor.getString(dateIdx),
                            cursor.getString(timeIdx),
                            cursor.getString(venueIdx),
                            cursor.getString(statusIdx),
                            cursor.getString(winnerIdx),
                            cursor.getString(resultIdx),
                            cursor.getString(secondIdx),
                            cursor.getString(thirdIdx)
                    );
                    matches.add(match);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return matches;
    }

    /**
     * Builds and formats the complete sports tournament rules using StringBuilder.
     * This avoids hardcoding a giant XML string and dynamically formats all rules.
     *
     * @return Formatted full rules string built with StringBuilder
     */
    public String buildFullRulesText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("  NMIMS MPSTME - FLAVIUMS SPORTS HUB\n");
        sb.append("     OFFICIAL TOURNAMENT REGULATIONS\n");
        sb.append("=========================================\n\n");

        sb.append("ELIGIBILITY & BRANCH SQUADS:\n");
        sb.append("• Participating Departments: MBA Tech CE, BTech CE, CE, IT, AIML, CSDS.\n");
        sb.append("• For sports designated as 'ONE team per branch only', squads span\n");
        sb.append("  across 1st, 2nd, and 3rd year students combined into one squad.\n\n");
        sb.append("SPORTS DISCIPLINES & RULES:\n\n");

        List<Sport> sports = getAllSports();
        int counter = 1;
        for (Sport sport : sports) {
            sb.append(counter++).append(". ").append(sport.getName()).append("\n");
            sb.append("   • Category: ").append(sport.getCategory()).append(" | Gender: ").append(sport.getGender()).append("\n");
            sb.append("   • Team Rule: ").append(sport.getTeamSizeRule()).append("\n");
            sb.append("   • Squad Restriction: ")
              .append(sport.isOneTeamPerBranchOnly() ? "Strictly ONE team per branch" : "Branch may enter multiple teams")
              .append("\n\n");
        }

        sb.append("CODE OF CONDUCT:\n");
        sb.append("All student athletes must wear official department jerseys and report 15 minutes prior to scheduled fixture time.\n");

        return sb.toString();
    }
}
