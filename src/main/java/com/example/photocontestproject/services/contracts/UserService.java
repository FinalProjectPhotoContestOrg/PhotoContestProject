package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers(String username, String firstname, String lastName);

    User getByUsername(String username);

    User updateUser(User user);

    User getUserById(int id);

    User getUserByEmail(String email);

    User createUser(User user);

    public void deleteUserById(int id);

    List<User> getMasters();

    List<User> getUsersByRole(Role role);

    int getNextRankPoints(int currentPoints);

    List<User> getUsersSortedByPoints();

    boolean isUserJurorToContest(User user, Entry entry);
}
