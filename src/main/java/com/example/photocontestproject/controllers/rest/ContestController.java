package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.ContestInDto;
import com.example.photocontestproject.dtos.in.EntryInDto;
import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.ContestMapper;
import com.example.photocontestproject.mappers.EntryMapper;
import com.example.photocontestproject.mappers.RatingMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/contests")
public class ContestController {
    private final ContestService contestService;
    private final RatingService ratingService;
    private final EntryService entryService;
    private final ContestMapper contestMapper;
    private final EntryMapper entryMapper;
    private final RatingMapper ratingMapper;
    private final AuthenticationHelper authenticationHelper;


    @Autowired
    public ContestController(ContestService contestService, EntryService entryService, ContestMapper contestMapper, EntryMapper entryMapper, RatingMapper ratingMapper, RatingService ratingService, AuthenticationHelper authenticationHelper) {
        this.contestService = contestService;
        this.entryService = entryService;
        this.contestMapper = contestMapper;
        this.entryMapper = entryMapper;
        this.ratingMapper = ratingMapper;
        this.ratingService = ratingService;

        this.authenticationHelper = authenticationHelper;
    }

    @PostMapping
    public Contest createContest(@RequestBody ContestInDto contestInDto, @RequestHeader HttpHeaders headers) {
        Contest contest;
        User user;
        try {
            user = authenticationHelper.tryGetUser(headers);
            contest = contestMapper.fromDto(contestInDto);
            return contestService.createContest(contest, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping
    public List<Contest> getAllContests(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(required = false) ContestType type,
                                        @RequestParam(required = false) ContestPhase phase) {
        return contestService.getAllContests(title, category, type, phase);
    }

    @GetMapping("/{id}")
    public Contest getContest(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return contestService.getContestById(id);
    }


    @DeleteMapping("/{id}")
    public void deleteContest(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(headers);
            contestService.deleteContest(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/{id}/phase")
    public Contest changePhase(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(headers);
            return contestService.changePhase(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/{contestId}/entries")
    public Set<Entry> getEntriesByContest(@PathVariable int contestId){
        Contest contest = contestService.getContestById(contestId);
        if (contest.getEntries().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return contest.getEntries();
    }
    @PostMapping("/{contestId}/entries")
    public Entry createEntryForContest(@PathVariable int contestId, @RequestBody EntryInDto entryInDto,
                                       @RequestHeader HttpHeaders headers) {
        Entry entry;
        User user;
        Contest contest;
        try {
            user = authenticationHelper.tryGetUser(headers);
            entry = entryMapper.fromDto(entryInDto);
            contest = contestService.getContestById(contestId);
            return contestService.createEntryForContest(entry, user, contest);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @PostMapping("/{contestId}/entries/{entryId}/ratings")
    public Rating rateEntry(@PathVariable int contestId, @PathVariable int entryId,
                            @Valid @RequestBody RatingDto ratingDto, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            int jurorId = user.getId();
            Contest contest = contestService.getContestById(contestId);
            Entry entry = entryService.getEntryById(entryId);
            if (!entry.getContest().getId().equals(contest.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The entry does not belong to the specified contest.");
            }
            if (contest.getContestPhase() != ContestPhase.PhaseII){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entries can only be rated during PhaseII.");
            }
            Rating rating = ratingMapper.fromDto(ratingDto, entry, jurorId);
            return ratingService.createRating(rating);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{contestId}/entries/{entryId}/ratings/{ratingId}")
    public Rating updateRatingToEntry(@PathVariable int contestId, @PathVariable int entryId,
                               @PathVariable int ratingId, @Valid @RequestBody RatingDto ratingDto,
                               @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Contest contest = contestService.getContestById(contestId);
            Entry entry = entryService.getEntryById(entryId);
            if (!contest.getEntries().contains(entry)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entry does not belong to the specified contest.");
            }
            Rating existingRating = ratingService.getRatingById(ratingId);
            int oldScore = existingRating.getScore();
            if (existingRating.getEntry().getId() != entryId) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating does not belong to the specified entry.");
            }
            if (!existingRating.getEntry().getContest().getId().equals(contestId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entry does not belong to the specified contest.");
            }
            Rating updatedRating = ratingMapper.fromDto(existingRating, ratingDto);
            return ratingService.updateRating(oldScore, updatedRating, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @DeleteMapping("/{contestId}/entries/{entryId}/ratings/{ratingId}")
    public void deleteRatingToEntry(@PathVariable int contestId, @PathVariable int entryId,
                              @PathVariable int ratingId, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Contest contest = contestService.getContestById(contestId);
            Entry entry = entryService.getEntryById(entryId);
            if (!contest.getEntries().contains(entry)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entry does not belong to the specified contest.");
            }

            Rating existingRating = ratingService.getRatingById(ratingId);
            if (existingRating.getEntry().getId() != entryId) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating does not belong to the specified entry.");
            }

            if (!existingRating.getEntry().getContest().getId().equals(contestId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entry does not belong to the specified contest.");
            }
            ratingService.deleteRating(ratingId, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
