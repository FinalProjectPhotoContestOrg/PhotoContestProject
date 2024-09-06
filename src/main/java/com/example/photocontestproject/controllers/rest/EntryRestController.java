package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.EntryInDto;
import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.EntryMapper;
import com.example.photocontestproject.mappers.RatingMapper;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryRestController {
    private final EntryService entryService;
    private final EntryMapper entryMapper;
    private final AuthenticationHelper authenticationHelper;
    private final RatingService ratingService;


    @Autowired
    public EntryRestController(EntryService entryService, EntryMapper entryMapper, AuthenticationHelper authenticationHelper,
                               RatingService ratingService) {
        this.entryService = entryService;
        this.entryMapper = entryMapper;
        this.authenticationHelper = authenticationHelper;
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<Entry> getAllEntries(@RequestParam(required = false) String title) {
        return entryService.getAllEntries(title);
    }

    @GetMapping("/{id}")
    public Entry getEntryById(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return entryService.getEntryById(id);
    }

    @PostMapping
    public Entry createEntry(@RequestBody EntryInDto entryInDto, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Entry entry = entryMapper.fromDto(entryInDto, user);
            if (entry.getContest().getContestPhase() != ContestPhase.PhaseI){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entries can only be created during PhaseI.");
            }
            return entryService.createEntry(entry, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{entryId}/ratings")
    public List<Rating> getEntryRatings(@PathVariable int entryId, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return ratingService.getRatingsForEntry(entryId, user).stream().toList();
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEntryById(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(headers);
            entryService.deleteEntryById(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
