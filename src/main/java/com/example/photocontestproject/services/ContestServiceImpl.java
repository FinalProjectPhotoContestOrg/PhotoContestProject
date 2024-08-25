package com.example.photocontestproject.services;

import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.repositories.ContestRepository;
import com.example.photocontestproject.services.contracts.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestServiceImpl implements ContestService {
    private ContestRepository contestRepository;
    @Autowired
    public ContestServiceImpl(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

    @Override
    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    @Override
    public Contest getContestById(int id) {
        return contestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Contest"));
    }

    @Override
    public Contest createContest(Contest contest) {
        return contestRepository.save(contest);
    }

    @Override
    public void deleteContest(int id) {
        contestRepository.deleteById(id);
    }
}
