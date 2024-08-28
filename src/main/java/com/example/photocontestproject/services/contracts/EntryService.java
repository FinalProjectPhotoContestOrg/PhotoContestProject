package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;

import java.util.List;

public interface EntryService {
    Entry createEntry(Entry entry, User user);

    Entry getEntryById(int id);

    Entry updateEntry(Entry entry);

    List<Entry> getAllEntries(String title);

    void deleteEntryById(int id, User user);
}
