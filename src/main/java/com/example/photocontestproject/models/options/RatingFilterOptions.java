package com.example.photocontestproject.models.options;

import java.util.Optional;

public class RatingFilterOptions {
    private Optional<Integer> minScore;
    private Optional<Integer> maxScore;
    private Optional<String> comment;
    private Optional<Boolean> categoryMismatch;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;
    private Optional<Integer> page;
    private Optional<Integer> size;
    public RatingFilterOptions(Integer minScore, Integer maxScore, String comment, Boolean categoryMismatch, String sortBy, String sortOrder, Integer page, Integer size) {
        this.minScore = Optional.ofNullable(minScore);
        this.maxScore = Optional.ofNullable(maxScore);
        this.comment = Optional.ofNullable(comment);
        this.categoryMismatch = Optional.ofNullable(categoryMismatch);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
        this.page = Optional.ofNullable(page);
        this.size = Optional.ofNullable(size);
    }

    public RatingFilterOptions() {
        this.minScore = Optional.empty();
        this.maxScore = Optional.empty();
        this.comment = Optional.empty();
        this.categoryMismatch = Optional.empty();
        this.sortBy = Optional.empty();
        this.sortOrder = Optional.empty();
        this.page = Optional.empty();
        this.size = Optional.empty();
    }

    public Optional<Integer> getMinScore() {
        return minScore;
    }

    public void setMinScore(Optional<Integer> minScore) {
        this.minScore = minScore;
    }

    public Optional<Integer> getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Optional<Integer> maxScore) {
        this.maxScore = maxScore;
    }

    public Optional<String> getComment() {
        return comment;
    }

    public void setComment(Optional<String> comment) {
        this.comment = comment;
    }

    public Optional<Boolean> getCategoryMismatch() {
        return categoryMismatch;
    }

    public void setCategoryMismatch(Optional<Boolean> categoryMismatch) {
        this.categoryMismatch = categoryMismatch;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(Optional<String> sortBy) {
        this.sortBy = sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Optional<String> sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Optional<Integer> getPage() {
        return page;
    }

    public void setPage(Optional<Integer> page) {
        this.page = page;
    }

    public Optional<Integer> getSize() {
        return size;
    }

    public void setSize(Optional<Integer> size) {
        this.size = size;
    }
}
