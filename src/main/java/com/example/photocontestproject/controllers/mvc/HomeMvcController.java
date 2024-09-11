package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.services.contracts.ContestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {
    private final ContestService contestService;

    public HomeMvcController(ContestService contestService) {
        this.contestService = contestService;
    }

    @ModelAttribute("contests")
    public List<Contest> populateContests() {
        return contestService.getAllContests(null, null, null, null);
    }

    @GetMapping
    public String getHomeView(Model model) {
        List<Entry> recentWinners = new ArrayList<>();
        List<Contest> finishedContests = contestService.getAllContests(null, null, null, ContestPhase.Finished);
        for (Contest contest : finishedContests) {
            recentWinners.add(contest.getEntries().getFirst());
            if (recentWinners.size() == 3) {
                break;
            }
        }
        if (recentWinners.size() < 3) {
            for (Contest contest : finishedContests) {
                recentWinners.add(contest.getEntries().get(1));
                if (recentWinners.size() == 3) {
                    break;
                }
            }
        }


        model.addAttribute("recentWinners", recentWinners);


        return "HomeView";
    }
}
