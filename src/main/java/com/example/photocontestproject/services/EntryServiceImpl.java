package com.example.photocontestproject.services;

import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.services.contracts.EntryService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryServiceImpl implements EntryService {
    public static final String ERROR_NO_PERMISSION_MESSAGE = "You do not have permission to perform this operation";
    private final EntryRepository entryRepository;
    @Autowired
    public EntryServiceImpl(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    @Override
    public Entry createEntry(Entry entry, User user) {
        throwIfUserIsOrganizer(user);
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
        return entryRepository.findAll((root, query, cb)->{
            Predicate predicate = cb.conjunction();
            if (title != null && !title.isEmpty()){
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + title + "%"));
            }
            return predicate;
        });
    }

    @Override
    public void deleteEntryById(int id, User user) {
        throwIfUserIsNotOrganizer(user);
        entryRepository.deleteById(id);
    }
    private void throwIfUserIsOrganizer(User user) {
        if (user.getRole().name().equals("Organizer")) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }
    private void throwIfUserIsNotOrganizer(User user) {
        if (user.getRole().name().equals("Junkie")) {
            throw new AuthorizationException(ERROR_NO_PERMISSION_MESSAGE);
        }
    }
}
