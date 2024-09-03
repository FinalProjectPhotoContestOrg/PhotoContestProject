package com.example.photocontestproject.services;

import com.example.photocontestproject.exceptions.DuplicateEntityException;
import com.example.photocontestproject.exceptions.EmailException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.external.EmailValidator;
import com.example.photocontestproject.external.service.EmailService;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.repositories.UserRepository;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
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
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User"));
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User"));
    }

    @Override
    public User createUser(User user) {
        throwIfUserIsDuplicate(user.getUsername());
        if (!EmailValidator.validateEmail(user.getEmail())) {
            throw new EmailException("Invalid email");
        }
        emailService.sendEmail(user.getEmail(), user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    public void throwIfUserIsDuplicate(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            throw new DuplicateEntityException("User", "username", userOptional.get().getUsername());
        }
    }
}
