package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.in.EntryInDto;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntryMapper {
    private final UserService userService;
//    private final ContestService contestService;
    @Autowired
    public EntryMapper(UserService userService) {
        this.userService = userService;
    }

    public Entry fromDto(EntryInDto entryInDto) {
        try {
            Entry entry = new Entry();
            User user = userService.getUserById(entryInDto.getParticipantId());
            entry.setTitle(entryInDto.getTitle());
            entry.setStory(entryInDto.getStory());
            entry.setContest(new Contest());
            return entry;
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}
