package com.example.photocontestproject.mappers;

import com.example.photocontestproject.dtos.ContestDto;
import com.example.photocontestproject.dtos.in.ContestInDto;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ContestMapper {
    private final UserService userService;
    @Autowired
    public ContestMapper(UserService userService) {
        this.userService = userService;
    }

    public Contest fromDto(ContestInDto contestInDto, User user) {
        Contest contest = new Contest();
        contest.setTitle(contestInDto.getTitle());
        contest.setCategory(contestInDto.getCategory());
        contest.setContestType(ContestType.Open);
        contest.setContestPhase(ContestPhase.PhaseI);
        contest.setPhase1End(contestInDto.getPhase1End());
        contest.setPhase2End(contestInDto.getPhase2End());
        contest.setCreatedAt(Timestamp.from(Instant.now()));
        try {
            if (user.getRole().equals(Role.Junkie)) {
                throw new AuthorizationException("You are not authorized to organize this contest");
            }
            contest.setOrganizer(user);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }

        return contest;
    }
    public Contest fromDto(ContestDto contestInDto) {
        Contest contest = new Contest();
        contest.setTitle(contestInDto.getTitle());
        contest.setCategory(contestInDto.getCategory());
        contest.setContestPhase(ContestPhase.PhaseI);
        contest.setContestType(contestInDto.getContestType());
        contest.setPhase1End(parseDateTimeToTimestamp(contestInDto.getPhase1End()));
        contest.setPhase2End(parseDateTimeToTimestamp(contestInDto.getPhase2End()));
        contest.setCoverPhotoUrl(contestInDto.getCoverPhotoUrl());
        contest.setCreatedAt(Timestamp.from(Instant.now()));
        return contest;
    }
    private Timestamp parseDateTimeToTimestamp(String dateTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
            return Timestamp.valueOf(localDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid date-time format: " + dateTime);
        }
    }
}
