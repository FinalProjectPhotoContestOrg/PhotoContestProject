package com.example.photocontestproject;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;

import java.util.ArrayList;
import java.util.List;

public class TestHelper {

    public static User createJunkieUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("junkie");
        user.setEmail("junkie@gmail.com");
        user.setPasswordHash("junkie");
        user.setRole(Role.Junkie);
        return user;
    }

    public static User createOrganizerUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("organizer");
        user.setEmail("organizer@gmail.com");
        user.setPasswordHash("organizer");
        user.setRole(Role.Organizer);
        return user;
    }

    public static List<Contest> createListOfContests() {
        List<Contest> mockContests = new ArrayList<>();
        Contest contest1 = new Contest();
        contest1.setTitle("Title1");
        contest1.setCategory("Category1");
        contest1.setContestType(ContestType.Open);
        contest1.setContestPhase(ContestPhase.PhaseI);
        mockContests.add(contest1);

        Contest contest2 = new Contest();
        contest2.setTitle("Title2");
        contest2.setCategory("Category2");
        contest2.setContestType(ContestType.Invitational);
        contest2.setContestPhase(ContestPhase.PhaseII);
        mockContests.add(contest2);

        return mockContests;
    }

    public static List<Entry> createListOfEntries() {
        List<Entry> entries = new ArrayList<>();

        Entry entry1 = new Entry();
        entry1.setTitle("Title1");
        entries.add(entry1);

        Entry entry2 = new Entry();
        entry2.setTitle("Title2");
        entries.add(entry2);

        Entry entry3 = new Entry();
        entry3.setTitle("Title3");
        entries.add(entry3);

        return entries;
    }

    public static List<User> createListOfUsers() {
        List<User> users = new ArrayList<>();

        User user1 = new User();
        user1.setUsername("User1");
        users.add(user1);

        User user2 = new User();
        user2.setUsername("User2");
        users.add(user2);

        User user3 = new User();
        user3.setUsername("User3");
        users.add(user3);

        return users;
    }

    public s
}
