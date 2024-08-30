package com.example.photocontestproject.services;

import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.specifications.RatingSpecification;
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
    public RatingServiceImpl(RatingRepository ratingRepository, EntryRepository entryRepository,
                             UserRepository userRepository, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public Rating createRating(Rating rating) {
        User juror = userService.getUserById(rating.getJuror().getId());
        User user = userService.getUserById(rating.getEntry().getParticipant().getId());
        throwIfNotOrganizer(juror);
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
        //TODO default 0 for mismatch
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
    public Rating updateRating(int oldScore, Rating ratingDetails, User user) {
        throwIfNotOrganizer(user);
        ratingDetails.getEntry().getContest().getJurors(); //TODO check using this type of ckecking through all jurors to check if the user is cocrect for checking
        User participant = ratingDetails.getEntry().getParticipant();
        int currentPoints = participant.getPoints();
        currentPoints -= oldScore;
        currentPoints += ratingDetails.getScore();
        participant.setPoints(currentPoints);
        updateRanking(participant);
        userRepository.save(participant);
        return ratingRepository.save(ratingDetails);
        //TODO add for juror too somehow using the contest...
    }

    @Override
    @Transactional
    public void deleteRating(int id, User user) {
        throwIfNotOrganizer(user);
        Rating ratingToDelete = ratingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Rating"));
        User participant = ratingToDelete.getEntry().getParticipant();
        int currentPoints = participant.getPoints();
        currentPoints -= ratingToDelete.getScore();
        participant.setPoints(currentPoints);
        updateRanking(participant);
        ratingToDelete.getEntry().getRatings().removeIf(rating -> rating.getId() == id);
        ratingToDelete.getJuror().getRatings().removeIf(rating -> rating.getId() == id);
        userRepository.save(participant);
        entryRepository.save(ratingToDelete.getEntry());
        ratingRepository.delete(ratingToDelete);
        //TODO add for juror too somehow using the contest...
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
    private void updateRanking(User participant) {
        int currentPoints = participant.getPoints();
        if (currentPoints >= 1001) {
            participant.setRanking(Ranking.WiseAndBenevolentPhotoDictator);
        } else if (currentPoints >= 151) {
            participant.setRanking(Ranking.Master);
        } else if (currentPoints >= 51) {
            participant.setRanking(Ranking.Enthusiast);
        } else {
            participant.setRanking(Ranking.Junkie);
        }
    }

}
