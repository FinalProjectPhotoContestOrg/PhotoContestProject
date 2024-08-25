package com.example.photocontestproject.services;

import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.repositories.EntryRepository;
import com.example.photocontestproject.services.contracts.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryServiceImpl implements EntryService {
    private final EntryRepository entryRepository;
    @Autowired
    public EntryServiceImpl(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    @Override
    public Entry createEntry(Entry entry) {
        return entryRepository.save(entry);
    }

    @Override
    public Entry getEntryById(int id) {
        return entryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entry"));
    }

    @Override
    public List<Entry> getAllEntries() {
        return entryRepository.findAll();
    }

    @Override
    public void deleteEntryById(int id) {
        entryRepository.deleteById(id);
    }
}
