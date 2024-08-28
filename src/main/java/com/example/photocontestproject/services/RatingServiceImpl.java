package com.example.photocontestproject.services;

import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.specifications.RatingSpecification;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.models.options.RatingFilterOptions;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.repositories.RatingRepository;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.contracts.RatingService;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, EntryRepository entryRepository, UserRepository userRepository, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public Rating createRating(Rating rating) {
        User user = userService.getUserById(rating.getEntry().getParticipant().getId());
        int currentPoints = user.getPoints();
        currentPoints += rating.getScore();
        user.setPoints(currentPoints);
        if (currentPoints > 1001) {
            user.setRanking(Ranking.WiseAndBenevolentPhotoDictator);
        } else if (currentPoints > 151) {
            user.setRanking(Ranking.Master);
        } else if (currentPoints > 51) {
            user.setRanking(Ranking.Enthusiast);
        }
        userService.updateUser(user);
        return ratingRepository.save(rating);
    }

    @Override
    public Rating getRatingById(int id, User user) {
        throwIfNotOrganizer(user);
        return ratingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Rating"));
    }

    @Override
    public Rating getRatingById(int id) {
        return ratingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Rating"));
    }

    @Override
    public Set<Rating> getAllRatings(RatingFilterOptions ratingFilterOptions, Pageable pageable) {
        Specification<Rating> specification = RatingSpecification.filterByOptions(ratingFilterOptions);
        return ratingRepository.findAll(specification, pageable);
    }

    @Override
    public Rating updateRating(Rating ratingDetails) {
        return ratingRepository.save(ratingDetails);
    }

    @Override
    @Transactional
    public void deleteRating(int id) {
        Rating ratingToDelete = ratingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Rating"));
        Entry entry = ratingToDelete.getEntry();
        entry.getRatings().removeIf(rating -> rating.getId() == id);
        User juror = ratingToDelete.getJuror();
        juror.getRatings().removeIf(rating -> rating.getId() == id);
        userRepository.save(juror);
        entryRepository.save(entry);
        ratingRepository.delete(ratingToDelete);
    }

    @Override
    public Set<Rating> getRatingsForEntry(int entryId, User user) {
        throwIfNotOrganizer(user);
        return ratingRepository.findByEntryId(entryId);
    }

    private void throwIfNotOrganizer(User user) {
        if (user.getRole() != Role.Organizer) {
            throw new AuthorizationException("You do not have access.");
        }
    }
}
