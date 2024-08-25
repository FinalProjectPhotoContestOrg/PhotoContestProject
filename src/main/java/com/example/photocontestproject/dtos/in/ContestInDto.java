package com.example.photocontestproject.dtos.in;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;

import java.sql.Timestamp;

public class ContestInDto {
    private String title;
    private String category;
    private Timestamp phase1End;
    private Timestamp phase2End;
    private int organizerId;


    public ContestInDto() {
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

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }
}
