package com.example.photocontestproject.services;

import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.external.service.EmailService;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.contracts.EntryService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

@Service
public class EntryServiceImpl implements EntryService {
    public static final String ERROR_NO_PERMISSION_MESSAGE = "You do not have permission to perform this operation";
    public static final String NOT_INVITED_TO_CONTEST_ERROR_MESSAGE = "You are not invited to this contest and can't enter.";

    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public EntryServiceImpl(EntryRepository entryRepository,
                            UserRepository userRepository,
                            EmailService emailService) {
        this.entryRepository = entryRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public Entry createEntry(Entry entry, User user) {
        throwIfUserIsOrganizer(user);
        throwIfUserIsJuror(user, entry);
        throwIfUserIsNotInvitedToContest(user, entry);
        /*if (!EmailValidator.validateEmail(user.getEmail())) {
            throw new EmailException("Invalid email");
        }*/
        emailService.sendEmailForEnteringInContest(user.getEmail(),
                user.getUsername(),
                entry.getContest().getTitle(),
                entry.getTitle());
        if (entry.getContest().getContestType().equals(ContestType.Open)) {
            int points = user.getPoints();
            points += 1;
            user.setPoints(points);
            userRepository.save(user);
        }

        return entryRepository.save(entry);
    }

    @Override
    public Entry getEntryById(int id) {
        return entryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entry"));
    }

    @Override
    public Entry updateEntry(Entry entry) {
        return entryRepository.save(entry);
    }

    @Override
    public List<Entry> getAllEntries(String title) {
        return entryRepository.findAll((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (title != null && !title.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + title + "%"));
            }
            return predicate;
        });
    }

    @Override
    public void deleteEntryById(int id, User user) {
        throwIfUserIsNotOrganizerOrAuthor(user, getEntryById(id));
        entryRepository.deleteById(id);
    }

    @Override
    public String getAverageRating(Entry entry) {
        float entryAvgScore = (float) entry.getEntryTotalScore() / entry.getRatings().size();
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(entryAvgScore);
    }

    @Override
    public int getEntryRankInContest(Entry entry) {
        List<Entry> sortedEntries = entry.getContest().getEntries().stream()
                .sorted(Comparator.comparing(Entry::getEntryTotalScore).reversed())
                .toList();
        return sortedEntries.indexOf(entry) + 1;
    }

    @Override
    public List<Contest> findContestsByUserId(int userId) {
        List<Entry> allEntries = entryRepository.findAll();
        return allEntries.stream()
                .filter(entry -> entry.getParticipant().getId().equals(userId))
                .map(Entry::getContest)
                .distinct()
                .toList();
    }

    private void throwIfUserIsOrganizer(User user) {
        if (user.getRole().equals(Role.Organizer)) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }

    public void throwIfUserIsJuror(User user, Entry entry) {
        if (entry.getContest().getJurors().stream()
                .anyMatch(juror -> juror.getId().equals(user.getId()))) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }

//    private void throwIfUserIsNotOrganizer(User user) {
//        if (!user.getRole().equals(Role.Organizer)) {
//            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
//        }
//    }

    private void throwIfUserIsNotOrganizerOrAuthor(User user, Entry entry) {
        if (!user.getRole().equals(Role.Organizer) && !entry.getParticipant().getId().equals(user.getId())) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }

    private void throwIfUserIsNotInvitedToContest(User user, Entry entry) {
        if (entry.getContest().getContestType().equals(ContestType.Invitational) && entry.getContest().getParticipants()
                .stream().noneMatch(participant -> participant.getId().equals(user.getId()))) {
            throw new AuthorizationException(NOT_INVITED_TO_CONTEST_ERROR_MESSAGE);
        }
    }
}
