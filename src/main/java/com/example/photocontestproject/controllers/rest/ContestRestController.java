package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.dtos.in.ContestInDto;
import com.example.photocontestproject.dtos.in.IdDto;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.ContestMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestRestController {
    private final ContestService contestService;
    private final ContestMapper contestMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public ContestRestController(ContestService contestService,
                                 ContestMapper contestMapper,
                                 AuthenticationHelper authenticationHelper) {
        this.contestService = contestService;
        this.contestMapper = contestMapper;

        this.authenticationHelper = authenticationHelper;
    }

    @PostMapping
    public Contest createContest(@RequestBody ContestInDto contestInDto,
                                 @RequestHeader HttpHeaders headers) {
        Contest contest;
        User user;
        try {
            user = authenticationHelper.tryGetUser(headers);
            contest = contestMapper.fromDto(contestInDto, user);
            return contestService.createContest(contest, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping
    public List<Contest> getAllContests(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) String category,
                                        @RequestParam(required = false) ContestType type,
                                        @RequestParam(required = false) ContestPhase phase) {
        return contestService.getAllContests(title, category, type, phase);
    }

    @GetMapping("/{id}")
    public Contest getContest(@PathVariable int id,
                              @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return contestService.getContestById(id);
    }

    @GetMapping("/{id}/jury")
    public List<User> getJurors(@PathVariable int id,
                                @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            return contestService.getJurors(id, loggedInUser);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{id}/jury")
    public Contest addJuror(@PathVariable int id,
                            @RequestBody IdDto idDto,
                            @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            int userId = idDto.getId();
            return contestService.addJuror(id, userId, loggedInUser);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/participants")
    public List<User> getParticipants(@PathVariable int id,
                                      @RequestBody IdDto idDto,
                                      @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            int userId = idDto.getId();
            return contestService.getParticipants(id, userId, loggedInUser);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("{id}/participants")
    public Contest addParticipant(@PathVariable int id,
                                  @RequestBody IdDto idDto,
                                  @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            int userId = idDto.getId();
            return contestService.addParticipant(id, userId, loggedInUser);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public void deleteContest(@PathVariable int id,
                              @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            contestService.deleteContest(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/{id}/phase")
    public Contest changePhase(@PathVariable int id,
                               @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return contestService.changePhase(id, user);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

}
