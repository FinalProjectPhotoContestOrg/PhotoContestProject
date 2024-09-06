package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface EntryService {
    Entry createEntry(Entry entry, User user);

    Entry getEntryById(int id);

    Entry updateEntry(Entry entry);

    List<Entry> getAllEntries(String title);

    void deleteEntryById(int id, User user);
}
