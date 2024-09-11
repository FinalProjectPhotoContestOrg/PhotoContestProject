package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Controller
@RequestMapping("/dashboard")
public class DashboardMvcController {
    private final ContestService contestService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final EntryService entryService;

    public DashboardMvcController(ContestService contestService, UserService userService, AuthenticationHelper authenticationHelper, EntryService entryService) {
        this.contestService = contestService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.entryService = entryService;
    }
    @GetMapping("/junkies")
    public String getDashboardView(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/login";
        }
        List<Contest> activeContests = contestService.getAllContests(null, null, null, ContestPhase.PhaseI);
        List<Contest> participatingContests = entryService.findContestsByUserId(user.getId());
        Set<Contest> finishedContests = entryService.findContestsByUserId(user.getId()).stream()
                .filter(contest -> contest.getContestPhase() == ContestPhase.Finished).collect(Collectors.toSet());
        int currentPoints = user.getPoints();
        Ranking currentRank = user.getRanking();
        int nextRankPoints = userService.getNextRankPoints(currentPoints);
        model.addAttribute("activeContests", activeContests);
        model.addAttribute("participatingContests", participatingContests);
        model.addAttribute("finishedContests", finishedContests);
        model.addAttribute("currentPoints", currentPoints);
        model.addAttribute("currentRank", currentRank);
        model.addAttribute("nextRankPoints", nextRankPoints);
        return "DashboardJunkiesView";
    }
}
