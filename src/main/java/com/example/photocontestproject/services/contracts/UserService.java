package com.example.photocontestproject.services.contracts;

import com.example.photocontestproject.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    User getByUsername(String username);

    User getUserById(int id);

    User createUser(User user);
    public void deleteUserById(int id);
}
