package com.example.photocontestproject.services;

import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.specifications.RatingSpecification;
import com.example.photocontestproject.models.Contest;
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
        throwIfNotOrganizerOrJuror(juror, rating.getEntry().getContest());
        Entry entry = rating.getEntry();
        int entryScore = entry.getEntryTotalScore();
        entryScore += rating.getScore();
        entry.setEntryTotalScore(entryScore);
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
        entryRepository.save(entry);
        userService.updateUser(user);
        return ratingRepository.save(rating);
        //TODO separate the logic for the user and the juror into methods
    }

    @Override
    public Rating getRatingById(int id, User user) {
        /*Rating rating = getRatingById(id);
        Contest contest = rating.getEntry().getContest();
        throwIfNotOrganizerOrJuror(user, contest);*/
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
        throwIfNotOrganizerOrJuror(user, ratingDetails.getEntry().getContest());
        Entry entry = ratingDetails.getEntry();
        int entryScore = entry.getEntryTotalScore();
        entryScore -= oldScore;
        entryScore += ratingDetails.getScore();
        entry.setEntryTotalScore(entryScore);
        User participant = ratingDetails.getEntry().getParticipant();
        int currentPoints = participant.getPoints();
        currentPoints -= oldScore;
        currentPoints += ratingDetails.getScore();
        participant.setPoints(currentPoints);
        updateRanking(participant);
        entryRepository.save(entry);
        userRepository.save(participant);
        return ratingRepository.save(ratingDetails);
    }

    @Override
    @Transactional
    public void deleteRating(int id, User user) {
        Rating ratingToDelete = ratingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Rating"));
        throwIfNotOrganizerOrJuror(user, ratingToDelete.getEntry().getContest());
        Entry entry = ratingToDelete.getEntry();
        int entryScore = entry.getEntryTotalScore();
        entryScore -= ratingToDelete.getScore();
        entry.setEntryTotalScore(entryScore);
        User participant = ratingToDelete.getEntry().getParticipant();
        int currentPoints = participant.getPoints();
        currentPoints -= ratingToDelete.getScore();
        participant.setPoints(currentPoints);
        updateRanking(participant);
        ratingToDelete.getEntry().getRatings().removeIf(rating -> rating.getId() == id);
        ratingToDelete.getJuror().getRatings().removeIf(rating -> rating.getId() == id);
        userRepository.save(participant);
        entryRepository.save(entry);
        ratingRepository.delete(ratingToDelete);
    }

    @Override
    public Set<Rating> getRatingsForEntry(int entryId, User user) {
        //throwIfNotOrganizer(user);
        return ratingRepository.findByEntryId(entryId);
    }

    private void throwIfNotOrganizerOrJuror(User user, Contest contest) {
        boolean isJuror = user.getJurorContests()
                .stream()
                .anyMatch(jurorContest -> jurorContest.getId().equals(contest.getId()));
        if (user.getRole() != Role.Organizer && !isJuror) {
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
