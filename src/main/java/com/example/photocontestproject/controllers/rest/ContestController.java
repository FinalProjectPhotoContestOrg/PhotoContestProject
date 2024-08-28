package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.ContestInDto;
import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.ContestMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestController {
    private final ContestService contestService;
    private final ContestMapper contestMapper;
    private final AuthenticationHelper authenticationHelper;


    @Autowired
    public ContestController(ContestService contestService, ContestMapper contestMapper, AuthenticationHelper authenticationHelper) {
        this.contestService = contestService;
        this.contestMapper = contestMapper;

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

//    @PostMapping("/entry/{entryId}/{jurorId}")
//    public Rating rateEntry(@PathVariable int entryId, @PathVariable int jurorId, @Valid @RequestBody RatingDto ratingDto) {
//        Rating rating = ratingMapper.fromDto(ratingDto, entryId, jurorId);
//        return ratingService.createRating(rating);
//        //TODO implement authorization and fix url and method
//
//    }

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
    //TODO ADD SWITCH PHASE ENDPOINT
    //TODO DON'T FORGET ENTRY ENDPOINT FROM RatingController to move here
}
