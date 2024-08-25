package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.ContestInDto;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.mappers.ContestMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.services.contracts.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestController {
    private final ContestService contestService;
    private final ContestMapper contestMapper;

    @Autowired
    public ContestController(ContestService contestService, ContestMapper contestMapper) {
        this.contestService = contestService;
        this.contestMapper = contestMapper;

    }

    @PostMapping
    public Contest createContest(@RequestBody ContestInDto contestInDto) {
        Contest contest;
        try {
            contest = contestMapper.fromDto(contestInDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return contestService.createContest(contest);
    }

    @GetMapping
    public List<Contest> getAllContests() {
        return contestService.getAllContests();
    }

    @GetMapping("/{id}")
    public Contest getContest(@PathVariable int id) {
        return contestService.getContestById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteContest(@PathVariable int id) {
        contestService.deleteContest(id);
    }
}
