package com.example.photocontestproject.enums;

public enum Role {
    Organizer("Organizer"),
    Junkie("Junkie");

    private final String name;

    Role(String name) {
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
