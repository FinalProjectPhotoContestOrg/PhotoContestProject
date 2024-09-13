package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.EntryInDto;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.EntryMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EntryRestController {
    private final EntryService entryService;
    private final EntryMapper entryMapper;
    private final AuthenticationHelper authenticationHelper;
    private final ContestService contestService;


    @Autowired
    public EntryRestController(EntryService entryService,
                               EntryMapper entryMapper,
                               AuthenticationHelper authenticationHelper,
                               ContestService contestService) {
        this.entryService = entryService;
        this.entryMapper = entryMapper;
        this.authenticationHelper = authenticationHelper;
        this.contestService = contestService;
    }

    @GetMapping("/entries")
    public List<Entry> getAllEntries(@RequestParam(required = false) String title) {
        return entryService.getAllEntries(title);
    }

    @GetMapping("/contests/{contestId}/entries")
    public List<Entry> getEntriesByContest(@PathVariable int contestId) {
        Contest contest = contestService.getContestById(contestId);
        if (contest.getEntries().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return contest.getEntries();
    }

    @GetMapping("/entries/{id}")
    public Entry getEntryById(@PathVariable int id,
                              @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return entryService.getEntryById(id);
    }

    @PostMapping("/contests/{contestId}/entries")
    public Entry createEntry(@RequestBody EntryInDto entryInDto,
                             @RequestHeader HttpHeaders headers,
                             @PathVariable int contestId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Entry entry = entryMapper.fromDto(entryInDto, user, contestId);
            if (entry.getContest().getContestPhase() != ContestPhase.PhaseI) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entries can only be created during PhaseI.");
            }
            return entryService.createEntry(entry, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("entries/{entryId}")
    public void deleteEntryById(@PathVariable int entryId,
                                @RequestHeader HttpHeaders headers) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(headers);
            entryService.deleteEntryById(entryId, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
