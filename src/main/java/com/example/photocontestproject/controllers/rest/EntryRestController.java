package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.EntryInDto;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.mappers.EntryMapper;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.services.contracts.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryRestController {
    private final EntryService entryService;
    private final EntryMapper entryMapper;

    @Autowired
    public EntryRestController(EntryService entryService, EntryMapper entryMapper) {
        this.entryService = entryService;
        this.entryMapper = entryMapper;
    }

    @GetMapping
    public List<Entry> getAllEntries() {
        return entryService.getAllEntries();
    }

    @GetMapping("/{id}")
    public Entry getEntryById(@PathVariable int id) {
        return entryService.getEntryById(id);
    }

    @PostMapping
    public Entry createEntry(@RequestBody EntryInDto entryInDto) {
        Entry entry;
        try {
            entry = entryMapper.fromDto(entryInDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return entryService.createEntry(entry);
    }
}
