package com.example.photocontestproject.enums;

public enum ContestPhase {
    PhaseI("Phase I"),
    PhaseII("Phase II"),
    Finished("Finished");

    private final String name;

    ContestPhase(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
