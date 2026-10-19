package com.flaviums.sportshub.models;

/**
 * Model class representing a sports fixture/match in Flaviums Sports Hub.
 * Contains fixture metadata (date, time, venue), participating teams,
 * status ("Upcoming" / "Completed"), winner, and results.
 * For individual events (e.g. Swimming, Athletics), secondPlace and thirdPlace are also recorded.
 */
public class Match {
    // Unique identifier for the match in SQLite database
    private int id;
    
    // Sport name (e.g., "Cricket (Boys)", "Athletics")
    private String sportName;
    
    // First participating branch team or participant
    private String teamA;
    
    // Second participating branch team or participant (or "Open/All" for multi-competitor events)
    private String teamB;
    
    // Scheduled match date in YYYY-MM-DD or display format
    private String date;
    
    // Scheduled match time (e.g., "10:00 AM")
    private String time;
    
    // Match venue location (e.g., "Main Cricket Ground", "Indoor Sports Complex")
    private String venue;
    
    // Current match status: "Upcoming" or "Completed"
    private String status;
    
    // Winner branch name or title
    private String winner;
    
    // Detailed score or result description (e.g. "Won by 24 runs", "3 - 1 penalties")
    private String resultNote;
    
    // Second place winner (used for individual events like Swimming, Athletics)
    private String secondPlace;
    
    // Third place winner (used for individual events like Swimming, Athletics)
    private String thirdPlace;

    /**
     * Default constructor.
     */
    public Match() {
    }

    /**
     * Parameterized constructor without id (for creating new matches before database insertion).
     */
    public Match(String sportName, String teamA, String teamB, String date, String time,
                 String venue, String status, String winner, String resultNote,
                 String secondPlace, String thirdPlace) {
        this.sportName = sportName;
        this.teamA = teamA;
        this.teamB = teamB;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.status = status;
        this.winner = winner;
        this.resultNote = resultNote;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    /**
     * Full parameterized constructor with id.
     */
    public Match(int id, String sportName, String teamA, String teamB, String date, String time,
                 String venue, String status, String winner, String resultNote,
                 String secondPlace, String thirdPlace) {
        this.id = id;
        this.sportName = sportName;
        this.teamA = teamA;
        this.teamB = teamB;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.status = status;
        this.winner = winner;
        this.resultNote = resultNote;
        this.secondPlace = secondPlace;
        this.thirdPlace = thirdPlace;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public String getSecondPlace() {
        return secondPlace;
    }

    public void setSecondPlace(String secondPlace) {
        this.secondPlace = secondPlace;
    }

    public String getThirdPlace() {
        return thirdPlace;
    }

    public void setThirdPlace(String thirdPlace) {
        this.thirdPlace = thirdPlace;
    }

    @Override
    public String toString() {
        return sportName + ": " + teamA + " vs " + teamB + " (" + status + ")";
    }
}
