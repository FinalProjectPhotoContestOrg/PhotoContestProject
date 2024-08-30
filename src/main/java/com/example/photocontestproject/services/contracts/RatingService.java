package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.models.options.RatingFilterOptions;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface RatingService {
    Rating createRating(Rating rating);

    Rating getRatingById(int id, User user);

    Rating getRatingById(int id);

    Set<Rating> getAllRatings(RatingFilterOptions ratingFilterOptions, Pageable pageable);

    Rating updateRating(int oldScore, Rating ratingDetails, User user);

    void deleteRating(int id, User user);

    Set<Rating> getRatingsForEntry(int entryId, User user);
}
