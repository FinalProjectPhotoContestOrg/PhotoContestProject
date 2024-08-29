package com.example.photocontestproject.models;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "contests")
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "category")
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ContestType contestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase")
    private ContestPhase contestPhase;

    @Column(name = "phase_1_end")
    private Timestamp phase1End;

    @Column(name = "phase_2_end")
    private Timestamp phase2End;

    @Column(name = "cover_photo_url")
    private String coverPhotoUrl;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "contest")
    private Set<Entry> entries;

    @ManyToMany
    @JoinTable(
            name = "contest_jurors",
            joinColumns =@JoinColumn(name = "contest_id"),
            inverseJoinColumns = @JoinColumn(name = "juror_id")
    )
    private Set<User> jurors;
    @ManyToMany
    @JoinTable(
            name = "contest_participants",
            joinColumns = @JoinColumn(name = "contest_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;
    public Contest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ContestType getContestType() {
        return contestType;
    }

    public void setContestType(ContestType contestType) {
        this.contestType = contestType;
    }

    public ContestPhase getContestPhase() {
        return contestPhase;
    }

    public void setContestPhase(ContestPhase contestPhase) {
        this.contestPhase = contestPhase;
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

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Entry> getEntries() {
        return entries;
    }

    public void setEntries(Set<Entry> entries) {
        this.entries = entries;
    }

    public Set<User> getJurors() {
        return jurors;
    }

    public void setJurors(Set<User> jurors) {
        this.jurors = jurors;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }
}
