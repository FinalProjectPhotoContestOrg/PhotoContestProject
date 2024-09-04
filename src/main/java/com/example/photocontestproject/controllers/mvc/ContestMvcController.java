package com.example.photocontestproject.controllers.mvc;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
