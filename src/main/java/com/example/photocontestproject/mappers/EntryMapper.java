package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.in.EntryInDto;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class EntryMapper {
    private final UserService userService;
    private final ContestService contestService;
    @Autowired
    public EntryMapper(UserService userService, ContestService contestService) {
        this.userService = userService;
        this.contestService = contestService;
    }

    public Entry fromDto(EntryInDto entryInDto) {
        try {
            Entry entry = new Entry();
            User user = userService.getUserById(entryInDto.getParticipantId());
            entry.setParticipant(user);
            entry.setTitle(entryInDto.getTitle());
            entry.setUploadedAt(Timestamp.from(Instant.now()));
            entry.setStory(entryInDto.getStory());
            Contest contest = contestService.getContestById(entryInDto.getContestId());
            entry.setContest(contest);
            return entry;
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}
