package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.RatingServiceImpl;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.RatingService;
import com.example.photocontestproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class RatingMapper {
    private final RatingService ratingService;

    private final UserService userService;
    @Autowired
    public RatingMapper(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
    }
    public Rating fromDto(Rating existingRating, RatingDto ratingDto){
        existingRating.setScore(ratingDto.getScore());
        existingRating.setComment(ratingDto.getComment());
        existingRating.setCategoryMismatch(ratingDto.isCategoryMismatch());
        return existingRating;
    }
    public RatingDto toDto(Rating rating){
        RatingDto ratingDto = new RatingDto();
        ratingDto.setScore(rating.getScore());
        ratingDto.setComment(rating.getComment());
        ratingDto.setCategoryMismatch(rating.isCategoryMismatch());
        return ratingDto;
    }

    public Rating fromDto(RatingDto ratingDto, Entry entry, int jurorId) {
        User juror = userService.getUserById(jurorId);
        Timestamp timestamp = Timestamp.from(Instant.now());
        Rating rating = new Rating();
        rating.setEntry(entry);
        rating.setJuror(juror);
        rating.setReviewedAt(timestamp);
        if (ratingDto.isCategoryMismatch()){
            rating.setScore(0);
            rating.setComment("Category is wrong");
            rating.setCategoryMismatch(true);
        } else {
            rating.setScore(ratingDto.getScore());
            rating.setComment(ratingDto.getComment());
            rating.setCategoryMismatch(false);
        }
        return rating;
    }
}
