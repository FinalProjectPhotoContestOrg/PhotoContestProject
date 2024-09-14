package com.example.photocontestproject.enums;

public enum ContestType {
    Open("Open"),
    Invitational("Invitational");

    private final String name;

    ContestType(String name) {
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
