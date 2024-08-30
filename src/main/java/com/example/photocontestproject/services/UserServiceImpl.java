package com.example.photocontestproject.services;

import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers(String username, String firstName, String lastName) {
        return userRepository.findAll((root, query, cb)->{
            Predicate predicate = cb.conjunction();
            if (username != null && !username.isEmpty()){
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + username + "%"));
            }
            if (firstName != null && !firstName.isEmpty()){
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + firstName + "%"));
            }
            if (lastName != null && !lastName.isEmpty()){
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + lastName + "%"));
            }
            return predicate;
        });
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }
}
