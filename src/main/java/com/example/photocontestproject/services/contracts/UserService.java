package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers(String username, String firstname, String lastName);

    User getByUsername(String username);

    User updateUser(User user);

    User getUserById(int id);

    User createUser(User user);

    public void deleteUserById(int id);
}
