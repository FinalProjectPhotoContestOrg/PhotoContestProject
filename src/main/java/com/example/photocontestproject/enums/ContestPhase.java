package com.example.photocontestproject.enums;

public enum ContestPhase {
    PhaseI("Phase I"),
    PhaseII("Phase II"),
    Finished("Finished");

    private final String name;

    ContestPhase(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
