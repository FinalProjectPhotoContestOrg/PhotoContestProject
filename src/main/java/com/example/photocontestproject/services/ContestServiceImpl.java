package com.example.photocontestproject.services;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.ContestRepository;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.services.contracts.ContestService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestServiceImpl implements ContestService {
    public static final String ERROR_NO_PERMISSION_MESSAGE = "You do not have permission to perform this operation";

    private final ContestRepository contestRepository;
    private final EntryRepository entryRepository;
    @Autowired
    public ContestServiceImpl(ContestRepository contestRepository, EntryRepository entryRepository) {
        this.contestRepository = contestRepository;
        this.entryRepository = entryRepository;
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
    @Override
    public Contest createContest(Contest contest, User user) {
        throwIfUserIsNotOrganizer(user);
        return contestRepository.save(contest);
    }

    @Override
    public Entry createEntryForContest(Entry entry, User user, Contest contest) {
        throwIfUserIsOrganizer(user);
        entry.setContest(contest);
        return entryRepository.save(entry);
    }

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
    private void throwIfUserIsOrganizer(User user) {
        if (user.getRole().name().equals("Organizer")) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }
}
