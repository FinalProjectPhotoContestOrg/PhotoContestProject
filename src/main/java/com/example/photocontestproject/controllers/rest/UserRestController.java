package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.UserInDto;
import com.example.photocontestproject.mappers.UserMapper;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/username/{username}")
    public User getByUsername(@PathVariable String username) {
        return userService.getByUsername(username);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid UserInDto userInDto) {
        User user = userMapper.fromDto(userInDto);
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
    }
}
