package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.models.Contest;

import java.util.List;

public interface ContestService {
    List<Contest> getAllContests();

    Contest getContestById(int id);

    Contest createContest(Contest contest);

    void deleteContest(int id);
}
