package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.in.UserInDto;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
@Component
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User fromDto(UserInDto userInDto) {
        User user = new User();
        user.setFirstName(userInDto.getFirstName());
        user.setLastName(userInDto.getLastName());
        String hashedPassword = passwordEncoder.encode(userInDto.getPassword());
        user.setPasswordHash(hashedPassword);
        user.setPasswordHash(userInDto.getPassword());
        user.setCreatedAt(Timestamp.from(Instant.now()));
        user.setRole(Role.Junkie);
        user.setRanking(Ranking.Junkie);
        user.setPoints(0);
        user.setUsername(userInDto.getUsername());
        user.setEmail(userInDto.getEmail());
        return user;
    }
}
