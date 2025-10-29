package com.vorobyevalx.workoutlogger.model;

public enum WorkoutStatus {
    PLANNED("Planned"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    WorkoutStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
