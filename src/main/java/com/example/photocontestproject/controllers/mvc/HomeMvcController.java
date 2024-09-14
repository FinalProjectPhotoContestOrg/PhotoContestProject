package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {
    private final ContestService contestService;
    private final UserService userService;
    private final EntryService entryService;

    public HomeMvcController(ContestService contestService, UserService userService, EntryService entryService) {
        this.contestService = contestService;
        this.userService = userService;
        this.entryService = entryService;
    }

    @ModelAttribute("contests")
    public List<Contest> populateContests() {
        return contestService.getAllContests(null, null, null, null);
    }

    @GetMapping
    public String getHomeView(Model model) {
        List<Entry> recentWinners = contestService.get3RecentWinners();
        Contest featuredContest = contestService.getFeaturedContest();
        List<User> userLeaderboard = userService.getUsersSortedByPoints();

        model.addAttribute("userLeaderboard", userLeaderboard);
        model.addAttribute("featuredContest", featuredContest);
        model.addAttribute("recentWinners", recentWinners);
        model.addAttribute("entryService", entryService); //TODO tova ne znam dali e ok

        return "HomeView";
    }
}
