package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.dtos.ContestDto;
import com.example.photocontestproject.dtos.in.ContestInDto;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.ContestMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/contests")
public class ContestMvcController {
    private final ContestService contestService;
    private final AuthenticationHelper authenticationHelper;
    private final ContestMapper contestMapper;

    public ContestMvcController(ContestService contestService, AuthenticationHelper authenticationHelper, ContestMapper contestMapper) {
        this.contestService = contestService;
        this.authenticationHelper = authenticationHelper;
        this.contestMapper = contestMapper;
    }
    @GetMapping("/{contestId}")
    public String showSingleContest(@PathVariable Integer contestId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/login";
        }
        try {
            Contest contest = contestService.getContestById(contestId);
            model.addAttribute("contest", contest);
            return "ContestView";
        } catch (EntityNotFoundException e){
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("notFound", e.getMessage());
            return "ErrorView";
        }
    }
    @GetMapping("/create")
    public String getCreateContestView(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/login";
        }
        model.addAttribute("contest", new ContestDto());
        return "CreateContestView";
    }
    @PostMapping("/create")
    public String handleContestCreation(@RequestParam("coverPhoto") MultipartFile file,
                                        @ModelAttribute("contest") ContestDto contestDto,
                                        @SessionAttribute("currentUser") User user,
                                        Model model) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            contestDto.setCoverPhotoUrl(base64Image);
            Contest contest = contestMapper.fromDto(contestDto);
            contest.setOrganizer(user);
            contestService.createContest(contest, user);
            return "redirect:/";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/contests/create";
        } catch (AuthorizationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("notOrganizer", e.getMessage());
            return "ErrorView";
        }
    }
}
