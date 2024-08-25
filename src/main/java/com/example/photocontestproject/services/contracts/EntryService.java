package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.models.Entry;

import java.util.List;

public interface EntryService {
    Entry createEntry(Entry entry);

    Entry getEntryById(int id);

    List<Entry> getAllEntries();

    void deleteEntryById(int id);
}
