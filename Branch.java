package com.flaviums.sportshub.models;

/**
 * Model class representing an Engineering Department/Branch at NMIMS MPSTME.
 * 6 designated branches: MBA Tech CE, BTech CE, CE, IT, AIML, CSDS.
 * Squads span across 1st, 2nd, and 3rd year students combined into one squad
 * for sports requiring one team per branch.
 */
public class Branch {
    // Unique identifier for the branch in SQLite database
    private int id;
    
    // Name of the engineering branch
    private String name;

    /**
     * Default constructor.
     */
    public Branch() {
    }

    /**
     * Parameterized constructor without id (for insertion).
     */
    public Branch(String name) {
        this.name = name;
    }

    /**
     * Full parameterized constructor with id.
     */
    public Branch(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return name;
    }
}
