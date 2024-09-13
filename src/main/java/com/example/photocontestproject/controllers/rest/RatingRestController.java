package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.RatingMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.models.options.RatingFilterOptions;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class RatingRestController {
    private final RatingService ratingService;
    private final AuthenticationHelper authenticationHelper;
    private final ContestService contestService;
    private final EntryService entryService;
    private final RatingMapper ratingMapper;

    @Autowired
    public RatingRestController(RatingService ratingService,
                                AuthenticationHelper authenticationHelper,
                                ContestService contestService,
                                EntryService entryService,
                                RatingMapper ratingMapper) {
        this.ratingService = ratingService;
        this.authenticationHelper = authenticationHelper;
        this.contestService = contestService;
        this.entryService = entryService;
        this.ratingMapper = ratingMapper;
    }

    @PostMapping("/contests/{contestId}/entries/{entryId}/ratings")
    public Rating createRatingForEntry(@PathVariable int contestId,
                                       @PathVariable int entryId,
                                       @Valid @RequestBody RatingDto ratingDto,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            int jurorId = user.getId();
            Contest contest = contestService.getContestById(contestId);
            Entry entry = entryService.getEntryById(entryId);
            if (!entry.getContest().getId().equals(contest.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The entry does not belong to the specified contest.");
            }
            if (contest.getContestPhase() != ContestPhase.PhaseII) {
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

    @GetMapping("/contests/{contestId}/entries/{entryId}/ratings")
    public Set<Rating> getRatingsForEntry(@PathVariable int entryId,
                                          @RequestHeader HttpHeaders headers,
                                          @PathVariable int contestId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Contest contestEntryIsIn = contestService.getContestById(contestId);
            if (contestEntryIsIn.getEntries().stream().noneMatch(entry -> entry.getId() == entryId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entry does not belong to the specified contest.");
            }
            return ratingService.getRatingsForEntry(entryId, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/contests/{contestId}/entries/{entryId}/ratings/{ratingId}")
    public Rating updateRatingForEntry(@PathVariable int contestId,
                                       @PathVariable int entryId,
                                       @PathVariable int ratingId,
                                       @Valid @RequestBody RatingDto ratingDto,
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

    @DeleteMapping("/contests/{contestId}/entries/{entryId}/ratings/{ratingId}")
    public void deleteRatingFromEntry(@PathVariable int contestId,
                                      @PathVariable int entryId,
                                      @PathVariable int ratingId,
                                      @RequestHeader HttpHeaders headers) {
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

    @GetMapping("/ratings")
    public Set<Rating> getAllRatings(@RequestHeader HttpHeaders headers,
                                     @RequestParam(required = false) Integer minScore,
                                     @RequestParam(required = false) Integer maxScore,
                                     @RequestParam(required = false) String comment,
                                     @RequestParam(required = false) Boolean categoryMismatch,
                                     @RequestParam(required = false) String sortBy,
                                     @RequestParam(required = false) String sortOrder,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        try {
            authenticationHelper.tryGetUser(headers);
            RatingFilterOptions ratingFilterOptions = new RatingFilterOptions(
                    minScore, maxScore, comment, categoryMismatch, sortBy, sortOrder, page, size);
            Pageable pageable = PageRequest.of(page, size);
            return ratingService.getAllRatings(ratingFilterOptions, pageable);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/contests/{contestId}/entries/{entryId}/ratings/{ratingId}")
    public Rating getRatingById(@PathVariable int ratingId,
                                @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return ratingService.getRatingById(ratingId, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/ratings/{id}")
    public void delete(@PathVariable int id,
                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            ratingService.deleteRating(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
