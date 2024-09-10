package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;

import java.util.List;
import java.util.Map;

public interface ContestService {
    List<Contest> getAllContests(String title, String category, ContestType type, ContestPhase phase);

    Contest getContestById(int id);

    Contest changePhase(int id, User user);

    Contest createContest(Contest contest, User user);

    Contest addJuror(int id, int userId, User loggedInUser);

    List<User> getJurors(int id, User loggedInUser);

    Contest addParticipant(int id, int userId, User loggedInUser);

    List<User> getParticipants(int id, int userId, User loggedInUser);

    //Entry createEntryForContest(Entry entry, User user/*, Contest contest*/);
    Map<Integer, String> getRanks(List<Entry> sortedEntries);

    void deleteContest(int id, User user);
}
