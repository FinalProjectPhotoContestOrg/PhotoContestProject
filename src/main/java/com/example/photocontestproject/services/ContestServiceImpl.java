package com.example.photocontestproject.services;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.ContestRepository;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestServiceImpl implements ContestService {
    public static final String ERROR_NO_PERMISSION_MESSAGE = "You do not have permission to perform this operation";
    public static final String ERROR_WRONG_CONTEST_TYPE_MESSAGE = "This Operation is for Invitational contests only.";

    private final ContestRepository contestRepository;
    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public ContestServiceImpl(ContestRepository contestRepository, EntryRepository entryRepository, UserRepository userRepository, UserService userService) {
        this.contestRepository = contestRepository;
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public List<Contest> getAllContests(String title, String category, ContestType type, ContestPhase phase) {
        return contestRepository.findAll((root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (title != null && !title.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + title + "%"));
            }
            if (category != null && !category.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("category"), category));
            }
            if (type != null) {
                predicate = cb.and(predicate, cb.equal(root.get("contestType"), type));
            }
            if (phase != null) {
                predicate = cb.and(predicate, cb.equal(root.get("contestPhase"), phase));
            }

            return predicate;
        });
    }

    @Override
    public Contest getContestById(int id) {
        return contestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Contest"));
    }

    @Override
    public Contest changePhase(int id, User user) {
        throwIfUserIsNotOrganizer(user);
        Contest contest = getContestById(id);
        ContestPhase currentPhase = contest.getContestPhase();
        currentPhase = currentPhase.ordinal() == ContestPhase.values().length - 1 ? ContestPhase.values()[0] : ContestPhase.values()[currentPhase.ordinal() + 1];
        contest.setContestPhase(currentPhase);
        return contestRepository.save(contest);
    }

    @Transactional
    @Override
    public Contest addJuror(int id, int userId, User loggedInUser) {
        throwIfUserIsNotOrganizer(loggedInUser);
        User userToAdd = userService.getUserById(userId);
        throwIfUserCantBeJuror(userToAdd);
        Contest contestToUpdate = contestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Contest"));
        contestToUpdate.getJurors().add(userToAdd);

        contestRepository.save(contestToUpdate);
        userRepository.save(userToAdd);
        return contestToUpdate;
    }

    @Override
    public List<User> getJurors(int id, User loggedInUser) {
        throwIfUserIsNotOrganizer(loggedInUser);
        Contest contest = contestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Contest"));
        return contest.getJurors().stream().toList();
    }

    @Override
    public Contest addParticipant(int id, int userId, User loggedInUser) {
        throwIfUserIsNotOrganizer(loggedInUser);
        User userToAdd = userService.getUserById(userId);
        Contest contestToUpdate = contestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Contest"));
        throwIfContestIsOpen(contestToUpdate);

        contestToUpdate.getParticipants().add(userToAdd);
        int points = userToAdd.getPoints();
        points += 3;
        userToAdd.setPoints(points);

        contestRepository.save(contestToUpdate);
        userRepository.save(userToAdd);

        return contestToUpdate;
    }

    @Override
    public List<User> getParticipants(int id, int userId, User loggedInUser) {
        throwIfUserIsNotOrganizer(loggedInUser);
        Contest contest = contestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Contest"));
        throwIfContestIsOpen(contest);

        return contest.getParticipants().stream().toList();
    }

    @Override
    public Contest createContest(Contest contest, User user) {
        throwIfUserIsNotOrganizer(user);
        return contestRepository.save(contest);
    }

    /*@Override
    public Entry createEntryForContest(Entry entry, User user*//*, Contest contest*//*) {
        throwIfUserIsOrganizer(user);
        //entry.setContest(contest);
        return entryRepository.save(entry);
    }*/

    @Override
    public void deleteContest(int id, User user) {
        throwIfUserIsNotOrganizer(user);
        contestRepository.deleteById(id);
    }

    private void throwIfUserIsNotOrganizer(User user) {
        if (user.getRole().name().equals("Junkie")) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }

    private void throwIfUserCantBeJuror(User user) {
        Ranking userRanking = user.getRanking();
        if (userRanking.equals(Ranking.Junkie) || userRanking.equals(Ranking.Enthusiast)) {
            throw new AuthorizationException("Only a user with a rank of Master or above to be a juror.");
        }
    }

    private void throwIfContestIsOpen(Contest contest) {
        if (contest.getContestType().equals(ContestType.Open)) {
            throw new AuthorizationException(ERROR_WRONG_CONTEST_TYPE_MESSAGE);
        }
    }

}
