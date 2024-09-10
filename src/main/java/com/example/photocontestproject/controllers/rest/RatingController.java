package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.RatingMapper;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.models.options.RatingFilterOptions;
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
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;
    private final AuthenticationHelper authenticationHelper;

    //TODO look over and refactor urls and code
    @Autowired
    public RatingController(RatingService ratingService, AuthenticationHelper authenticationHelper) {
        this.ratingService = ratingService;
        this.authenticationHelper = authenticationHelper;
    }
    @GetMapping
    public Set<Rating> getAllRatings(@RequestHeader HttpHeaders headers,
                                     @RequestParam(required = false) Integer minScore,
                                     @RequestParam(required = false) Integer maxScore,
                                     @RequestParam(required = false) String comment,
                                     @RequestParam(required = false) Boolean categoryMismatch,
                                     @RequestParam(required = false) String sortBy,
                                     @RequestParam(required = false) String sortOrder,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size){
        try {
            authenticationHelper.tryGetUser(headers);
            RatingFilterOptions ratingFilterOptions = new RatingFilterOptions(
                    minScore, maxScore, comment, categoryMismatch, sortBy,sortOrder,page, size);
            Pageable pageable = PageRequest.of(page, size);
            return ratingService.getAllRatings(ratingFilterOptions, pageable);
        } catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/{ratingId}")
    public Rating getRatingById(@PathVariable int ratingId, @RequestHeader HttpHeaders headers){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return ratingService.getRatingById(ratingId, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @GetMapping("/entries/{entryId}")
    public Set<Rating> getRatingsForEntry(@PathVariable int entryId, @RequestHeader HttpHeaders headers){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return ratingService.getRatingsForEntry(entryId, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            ratingService.deleteRating(id, user);
        }catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
