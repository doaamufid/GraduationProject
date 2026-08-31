package com.example.graduationproject.models.profile;

public class ChildStats {
    private int exercises;
    private int sessions;
    private int recoveryTreeVisits;
    private int calmCornerVisits;

    public ChildStats(int exercises, int sessions, int recoveryTreeVisits, int calmCornerVisits) {
        this.exercises = exercises;
        this.sessions = sessions;
        this.recoveryTreeVisits = recoveryTreeVisits;
        this.calmCornerVisits = calmCornerVisits;
    }

    public int getExercises() { return exercises; }
    public int getSessions() { return sessions; }
    public int getRecoveryTreeVisits() { return recoveryTreeVisits; }
    public int getCalmCornerVisits() { return calmCornerVisits; }
}
