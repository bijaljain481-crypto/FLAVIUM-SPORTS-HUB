package com.flaviums.sportshub.models;

/**
 * Model class representing a Sport event in Flaviums Sports Hub.
 * Stores sport metadata including category (Indoor/Outdoor), gender category,
 * team size regulations, and branch team restriction flags.
 */
public class Sport {
    // Unique identifier for the sport in SQLite database
    private int id;
    
    // Name of the sport (e.g., "Cricket (Boys)", "Badminton")
    private String name;
    
    // Category of the sport: "Indoor" or "Outdoor"
    private String category;
    
    // Target participant gender: "Boys", "Girls", or "Mixed"
    private String gender;
    
    // Short description of squad/team sizing rules (e.g. "7 players, 5 overs", "3-4 players per team", "Individual")
    private String teamSizeRule;
    
    // Whether a branch is strictly limited to entering only one combined squad
    private boolean oneTeamPerBranchOnly;

    /**
     * Default constructor.
     */
    public Sport() {
    }

    /**
     * Parameterized constructor without id (useful for new insertions before auto-increment ID generation).
     */
    public Sport(String name, String category, String gender, String teamSizeRule, boolean oneTeamPerBranchOnly) {
        this.name = name;
        this.category = category;
        this.gender = gender;
        this.teamSizeRule = teamSizeRule;
        this.oneTeamPerBranchOnly = oneTeamPerBranchOnly;
    }

    /**
     * Full parameterized constructor with id.
     */
    public Sport(int id, String name, String category, String gender, String teamSizeRule, boolean oneTeamPerBranchOnly) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.gender = gender;
        this.teamSizeRule = teamSizeRule;
        this.oneTeamPerBranchOnly = oneTeamPerBranchOnly;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTeamSizeRule() {
        return teamSizeRule;
    }

    public void setTeamSizeRule(String teamSizeRule) {
        this.teamSizeRule = teamSizeRule;
    }

    public boolean isOneTeamPerBranchOnly() {
        return oneTeamPerBranchOnly;
    }

    public void setOneTeamPerBranchOnly(boolean oneTeamPerBranchOnly) {
        this.oneTeamPerBranchOnly = oneTeamPerBranchOnly;
    }

    @Override
    public String toString() {
        return name;
    }
}
