package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;

import java.util.List;

public interface ContestService {
    List<Contest> getAllContests(String title, String category, ContestType type, ContestPhase phase);

    Contest getContestById(int id);

    Contest changePhase(int id, User user);

    Contest createContest(Contest contest, User user);
    //Entry createEntryForContest(Entry entry, User user/*, Contest contest*/);

    void deleteContest(int id, User user);
}
