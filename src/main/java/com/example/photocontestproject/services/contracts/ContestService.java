package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.User;

import java.util.List;

public interface ContestService {
    List<Contest> getAllContests(String title, String category, ContestType type, ContestPhase phase);

    Contest getContestById(int id);

    Contest createContest(Contest contest, User user);

    void deleteContest(int id, User user);
}
