package com.example.photocontestproject.services;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.ContestRepository;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.RatingService;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ContestServiceImpl implements ContestService {
    public static final String ERROR_NO_PERMISSION_MESSAGE = "You do not have permission to perform this operation";
    public static final String ERROR_WRONG_CONTEST_TYPE_MESSAGE = "This Operation is for Invitational contests only.";
    public static final String NOT_A_MASTER_OR_ABOVE_ERROR_MESSAGE = "Only a user with a rank of Master or above to be a juror.";

    private final ContestRepository contestRepository;
    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RatingService rankingService;

    @Autowired
    public ContestServiceImpl(ContestRepository contestRepository,
                              EntryRepository entryRepository,
                              UserRepository userRepository,
                              UserService userService,
                              RatingService rankingService) {
        this.contestRepository = contestRepository;
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.rankingService = rankingService;
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

    @Override
    public Map<Integer, String> getRanks(List<Entry> sortedEntries) {
        Map<Integer, String> ranks = new HashMap<>();
        for (int i = 0; i < sortedEntries.size(); i++) {
            ranks.put(i + 1, getOrdinalSuffix(i + 1));
        }
        return ranks;
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
    private String getOrdinalSuffix(int number) {
        if (number % 10 == 1 && number % 100 != 11) {
            return "st";
        } else if (number % 10 == 2 && number % 100 != 12) {
            return "nd";
        } else if (number % 10 == 3 && number % 100 != 13) {
            return "rd";
        } else {
            return "th";
        }
    }

    private void throwIfUserIsNotOrganizer(User user) {
        if (!user.getRole().equals(Role.Organizer)) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }

    private void throwIfUserCantBeJuror(User user) {
        Ranking userRanking = user.getRanking();
        if (userRanking.equals(Ranking.Junkie) || userRanking.equals(Ranking.Enthusiast)) {
            throw new AuthorizationException(NOT_A_MASTER_OR_ABOVE_ERROR_MESSAGE);
        }
    }

    private void throwIfContestIsOpen(Contest contest) {
        if (contest.getContestType().equals(ContestType.Open)) {
            throw new AuthorizationException(ERROR_WRONG_CONTEST_TYPE_MESSAGE);
        }
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void scheduledTask() {
        List<Contest> contests = contestRepository.findAll();

        for (Contest contest : contests) {
            if (contest.getContestPhase().equals(ContestPhase.PhaseI)) {
                Timestamp currentTime = Timestamp.from(Instant.now());
                if (currentTime.after(contest.getPhase1End())) {
                    contest.setContestPhase(ContestPhase.PhaseII);
                    contestRepository.save(contest);
                }
            }

            if (contest.getContestPhase().equals(ContestPhase.PhaseII)) {
                Timestamp currentTime = Timestamp.from(Instant.now());
                if (currentTime.after(contest.getPhase2End())) {
                    contest.setContestPhase(ContestPhase.Finished);
                    handleScoringWhenContestEnds(contest);
                    contestRepository.save(contest);
                }
            }
        }
    }

    @Transactional
    public void handleScoringWhenContestEnds(Contest contest) {
        if (contest.getContestPhase().equals(ContestPhase.Finished)) {
            List<Entry> entries = contest.getEntries();

            entries.sort((e1, e2) -> e2.getEntryTotalScore() - e1.getEntryTotalScore());

            int position = 1;
            for (int i = 0; i < entries.size(); i++) {
                if (position == 4) {
                    break;
                }
                Entry entry = entries.get(i);
                int entryTotalScore = entry.getEntryTotalScore();

                boolean isSharedSpot = false;

                for (int k = i + 1; k < entries.size(); k++) {
                    Entry potentialSharedSpotEntry = entries.get(k);
                    int potentialEntryTotalScore = potentialSharedSpotEntry.getEntryTotalScore();

                    if (potentialEntryTotalScore != entryTotalScore) {
                        break;
                    } else {
                        isSharedSpot = true;
                    }
                    calculateAndHandleUserPointsAdding(potentialSharedSpotEntry, isSharedSpot, position);
                    i++;
                }
                calculateAndHandleUserPointsAdding(entry, isSharedSpot, position);
                position++;
            }
        }
    }

    private void calculateAndHandleUserPointsAdding(Entry entry, boolean isSharedSpot, int position) {
        int userPoints = entry.getParticipant().getPoints();
        userPoints += calculateScore(isSharedSpot, position);
        entry.getParticipant().setPoints(userPoints);
        rankingService.updateRanking(entry.getParticipant());
        userRepository.save(entry.getParticipant());
    }

    private int calculateScore(boolean isSharedSpot, int position) {
        if (isSharedSpot) {
            switch (position) {
                case 1: return 40;
                case 2: return 25;
                case 3: return 10;
            }
        } else {
            switch (position) {
                case 1: return 50;
                case 2: return 35;
                case 3: return 20;
            }
        }
        return 0;
    }

}
