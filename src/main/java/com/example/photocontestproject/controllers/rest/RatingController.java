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
    private final RatingMapper ratingMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public RatingController(RatingService ratingService, RatingMapper ratingMapper, AuthenticationHelper authenticationHelper) {
        this.ratingService = ratingService;
        this.ratingMapper = ratingMapper;
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
    public Rating getRatingById(@PathVariable int ratingId){
        return ratingService.getRatingById(ratingId);
        //TODO implement authorization

    }
    @GetMapping("/entry/{entryId}")
    public Set<Rating> getRatingsForEntry(@PathVariable int entryId){
        return ratingService.getRatingsForEntry(entryId);
        //TODO implement authorization
    }

    @PutMapping("/{id}")
    public Rating updateRating(@PathVariable int id, @Valid @RequestBody RatingDto ratingDto){
        Rating updateRating = ratingMapper.fromDto(id, ratingDto);
        return ratingService.updateRating(updateRating);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        ratingService.deleteRating(id);
    }
}
