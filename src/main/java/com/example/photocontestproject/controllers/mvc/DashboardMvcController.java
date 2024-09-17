package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.Ranking;
import com.example.photocontestproject.enums.Role;
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

@Controller
@RequestMapping("/dashboard")
public class DashboardMvcController {
    private final ContestService contestService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;


    public DashboardMvcController(ContestService contestService,
                                  UserService userService,
                                  AuthenticationHelper authenticationHelper) {
        this.contestService = contestService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String handleDashboardRedirect(HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            session.setAttribute("redirectUrl", "/dashboard");
            return "redirect:/login";
        }
        if (user.getRole().equals(Role.Junkie)) {
            return "redirect:/dashboard/junkies";
        } else {
            return "redirect:/dashboard/organizer";
        }
    }

    @GetMapping("/junkies")
    public String getDashboardJunkieView(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            session.setAttribute("redirectUrl", "/dashboard/junkies");
            return "redirect:/login";
        }


        List<Contest> participatingContests = contestService.getUnFinishedContestsForUser(user);
        List<Contest> contestUserIsNorParticipatingIn = contestService.getContestsUserIsNotParticipatingIn(user);
        Set<Contest> finishedContestsForUser = contestService.getFinishedContestsForUser(user);
        List<Contest> jurorContests = contestService.getContestsWithJuror(user);
        int currentPoints = user.getPoints();
        Ranking currentRank = user.getRanking();
        int nextRankPoints = userService.getNextRankPoints(currentPoints);

        model.addAttribute("participatingContests", participatingContests);
        model.addAttribute("activeContests", contestUserIsNorParticipatingIn);
        model.addAttribute("finishedContests", finishedContestsForUser);
        model.addAttribute("jurorContests", jurorContests);
        model.addAttribute("currentPoints", currentPoints);
        model.addAttribute("currentRank", currentRank);
        model.addAttribute("nextRankPoints", nextRankPoints);
        return "DashboardJunkiesView";
    }


    @GetMapping("/organizer")
    public String getDashboardViewOrganizer(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            session.setAttribute("redirectUrl", "/dashboard/organizer");
            return "redirect:/login";
        }


        List<Contest> phaseIContests = contestService.getAllContests(null, null, null, ContestPhase.PhaseI);
        List<Contest> phaseIIContests = contestService.getAllContests(null, null, null, ContestPhase.PhaseII);
        List<Contest> finishedContests = contestService.getAllContests(null, null, null, ContestPhase.Finished);

        model.addAttribute("phaseIContests", phaseIContests);
        model.addAttribute("phaseIIContests", phaseIIContests);
        model.addAttribute("finishedContests", finishedContests);

        return "DashboardViewOrganizer";
    }
}
