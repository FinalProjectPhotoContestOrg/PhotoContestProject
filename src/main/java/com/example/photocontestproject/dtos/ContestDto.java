package com.example.photocontestproject.dtos;

import com.example.photocontestproject.enums.ContestType;

import java.sql.Timestamp;

public class ContestDto {
    private String title;
    private String category;
    /*private Timestamp phase1End;
    private Timestamp phase2End;*/
    private ContestType contestType;
    private String phase1End;
    private String phase2End;
    private String coverPhotoUrl;

    public ContestDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

/*
    public Timestamp getPhase1End() {
        return phase1End;
    }

    public void setPhase1End(Timestamp phase1End) {
        this.phase1End = phase1End;
    }

    public Timestamp getPhase2End() {
        return phase2End;
    }

    public void setPhase2End(Timestamp phase2End) {
        this.phase2End = phase2End;
    }
*/

    public String getPhase1End() {
        return phase1End;
    }

    public void setPhase1End(String phase1End) {
        this.phase1End = phase1End;
    }

    public String getPhase2End() {
        return phase2End;
    }

    public void setPhase2End(String phase2End) {
        this.phase2End = phase2End;
    }

    public ContestType getContestType() {
        return contestType;
    }

    public void setContestType(ContestType contestType) {
        this.contestType = contestType;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }
}
