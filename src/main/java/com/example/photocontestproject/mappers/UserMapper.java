package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.in.UserInDto;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.models.User;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;

@Component
public class UserMapper {

    public UserMapper() {
    }

    public User fromDto(UserInDto userInDto) {
        User user = new User();
        user.setFirstName(userInDto.getFirstName());
        user.setLastName(userInDto.getLastName());
        String hashedPassword = hashPassword(userInDto.getPassword());
        user.setPasswordHash(hashedPassword);
        user.setCreatedAt(Timestamp.from(Instant.now()));
        user.setRole(Role.Junkie);
        user.setRanking(Ranking.Junkie);
        user.setPoints(0);
        user.setUsername(userInDto.getUsername());
        user.setEmail(userInDto.getEmail());
        return user;
    }
    private String hashPassword(String password)  {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
