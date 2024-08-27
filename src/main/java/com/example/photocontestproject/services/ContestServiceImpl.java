package com.example.photocontestproject.services;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.ContestRepository;
import com.example.photocontestproject.services.contracts.ContestService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestServiceImpl implements ContestService {
    public static final String ERROR_NO_PERMISSION_MESSAGE = "You do not have permission to perform this operation";

    private ContestRepository contestRepository;
    @Autowired
    public ContestServiceImpl(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
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
    public Contest createContest(Contest contest, User user) {
        throwIfUserIsNotOrganizer(user);
        return contestRepository.save(contest);
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
}
