package com.example.photocontestproject.enums;

public enum ContestPhase {
    PhaseI,
    PhaseII,
    Finished;


    @Override
    public String toString() {
        return switch (this) {
            case PhaseI -> "Phase I";
            case PhaseII -> "Phase II";
            case Finished -> "Finished";
        };
    }
}
