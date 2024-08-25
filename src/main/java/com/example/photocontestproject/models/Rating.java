package com.example.photocontestproject.models;

import jakarta.persistence.*;

import java.security.Timestamp;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "photo_id", nullable = false)
    private Entry photo;

    @ManyToOne
    @JoinColumn(name = "juror_id", nullable = false)
    private User juror;

    @Column(name = "score")
    private Integer score;

    @Column(name = "comment")
    private String comment;

    @Column(name = "category_mismatch")
    private boolean categoryMismatch;

    @Column(name = "reviewed_at")
    private Timestamp reviewedAt;

    public Rating() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Entry getPhoto() {
        return photo;
    }

    public void setPhoto(Entry photo) {
        this.photo = photo;
    }

    public User getJuror() {
        return juror;
    }

    public void setJuror(User juror) {
        this.juror = juror;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
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

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
