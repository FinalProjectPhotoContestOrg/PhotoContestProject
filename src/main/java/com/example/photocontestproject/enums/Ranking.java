package com.example.photocontestproject.enums;

public enum Ranking {
    Junkie/*(50)*/,
    Enthusiast/*(150)*/,
    Master/*(1000)*/,
    WiseAndBenevolentPhotoDictator/*(1001)*/;
    /*private final int points;
    Ranking(int points) {
        this.points = points;
    }
    public int getPoints() {
        return points;
    }*/

    public static final int WISE_AND_BENEVOLENT_POINT_THRESHOLD = 1001;
    public static final int MASTER_POINT_THRESHOLD = 151;
    public static final int ENTHUSIAST_POINT_THRESHOLD = 51;
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
