package com.example.photocontestproject.enums;

public enum Ranking {
    Junkie("Junkie"),
    Enthusiast("Enthusiast"),
    Master("Master"),
    WiseAndBenevolentPhotoDictator("Wise and Benevolent Photo Dictator");

    public static final int WISE_AND_BENEVOLENT_POINT_THRESHOLD = 1001;
    public static final int MASTER_POINT_THRESHOLD = 151;
    public static final int ENTHUSIAST_POINT_THRESHOLD = 51;

    private final String name;

    Ranking(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static int getPointsToNextRank(int points) {
        if (points < ENTHUSIAST_POINT_THRESHOLD) {
            return ENTHUSIAST_POINT_THRESHOLD - points;
        } else if (points < MASTER_POINT_THRESHOLD) {
            return MASTER_POINT_THRESHOLD - points;
        } else if (points < WISE_AND_BENEVOLENT_POINT_THRESHOLD) {
            return WISE_AND_BENEVOLENT_POINT_THRESHOLD - points;
        } else {
            return 0;
        }
    }
}
