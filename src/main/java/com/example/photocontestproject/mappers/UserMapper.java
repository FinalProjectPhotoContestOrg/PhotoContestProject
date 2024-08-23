package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.in.UserInDto;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.models.User;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
@Component
public class UserMapper {

    public UserMapper() {
    }

    public User fromDto(UserInDto userInDto) {
        User user = new User();
        user.setFirstName(userInDto.getFirstName());
        user.setLastName(userInDto.getLastName());
        user.setPasswordHash(userInDto.getPassword());
        user.setCreatedAt(Timestamp.from(Instant.now()));
        user.setRole(Role.Junkie);
        user.setRanking(Ranking.Junkie);
        user.setPoints(0);
        user.setUsername(userInDto.getUsername());
        return user;
    }
}
