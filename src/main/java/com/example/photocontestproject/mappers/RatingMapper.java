package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.in.RatingDto;
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
    private final EntryService entryService;
    private final UserService userService;
    @Autowired
    public RatingMapper(RatingService ratingService, EntryService entryService, UserService userService) {
        this.ratingService = ratingService;
        this.entryService = entryService;
        this.userService = userService;
    }
    public Rating fromDto(RatingDto ratingDto, int entryId, int jurorId){
        Entry entry = entryService.getEntryById(entryId);
        User juror = userService.getUserById(jurorId);
        Timestamp timestamp = Timestamp.from(Instant.now());
        Rating rating = new Rating();
        rating.setScore(ratingDto.getScore());
        rating.setComment(ratingDto.getComment());
        rating.setCategoryMismatch(ratingDto.isCategoryMismatch());
        rating.setEntry(entry);
        rating.setJuror(juror);
        rating.setReviewedAt(timestamp);
        return rating;
    }
    public Rating fromDto(int id, RatingDto ratingDto){
        Rating existRating = ratingService.getRatingById(id);
        existRating.setScore(ratingDto.getScore());
        existRating.setComment(ratingDto.getComment());
        existRating.setCategoryMismatch(ratingDto.isCategoryMismatch());
        return existRating;
    }
    public RatingDto toDto(Rating rating){
        RatingDto ratingDto = new RatingDto();
        ratingDto.setScore(rating.getScore());
        ratingDto.setComment(rating.getComment());
        ratingDto.setCategoryMismatch(rating.isCategoryMismatch());
        return ratingDto;
    }

}
