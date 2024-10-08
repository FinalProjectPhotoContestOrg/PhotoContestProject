package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.EntryDto;
import com.example.photocontestproject.dtos.in.EntryInDto;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class EntryMapper {

    private final ContestService contestService;

    @Autowired
    public EntryMapper(ContestService contestService) {
        this.contestService = contestService;
    }

    public Entry fromDto(EntryInDto entryInDto, User user, int contestId) {
        try {
            Entry entry = new Entry();
            entry.setParticipant(user);
            entry.setTitle(entryInDto.getTitle());
            entry.setUploadedAt(Timestamp.from(Instant.now()));
            entry.setStory(entryInDto.getStory());
            entry.setPhotoUrl("http://example.com/photo6.jpg");
            Contest contest = contestService.getContestById(contestId);
            entry.setContest(contest);
            return entry;
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    public Entry fromDto(EntryDto entryDto, User user) {
        try {
            Entry entry = new Entry();
            entry.setParticipant(user);
            entry.setTitle(entryDto.getTitle());
            entry.setUploadedAt(Timestamp.from(Instant.now()));
            entry.setStory(entryDto.getStory());
            entry.setPhotoUrl(entryDto.getPhotoUrl());
            return entry;
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}
