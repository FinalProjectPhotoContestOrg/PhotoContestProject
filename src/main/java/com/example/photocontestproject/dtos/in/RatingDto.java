package com.example.photocontestproject.dtos.in;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RatingDto {
    @Min(value = 1, message = "Score must be at least 1")
    @Max(value = 11, message = "Score must be at bellow 11")
    private int score;
    @NotNull(message = "Comment can't be empty")
    private String comment;
    private boolean categoryMismatch;

    public RatingDto() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isCategoryMismatch() {
        return categoryMismatch;
    }

    public void setCategoryMismatch(boolean categoryMismatch) {
        this.categoryMismatch = categoryMismatch;
    }
}
