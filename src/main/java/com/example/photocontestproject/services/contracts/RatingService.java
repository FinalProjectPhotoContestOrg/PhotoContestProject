package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.options.RatingFilterOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RatingService {
    Rating createRating(Rating rating);
    Rating getRatingById(int id);
    Set<Rating> getAllRatings(RatingFilterOptions ratingFilterOptions, Pageable pageable);
    Rating updateRating(Rating ratingDetails);
    void deleteRating(int id);
    Set<Rating> getRatingsForEntry(int entryId);
}
