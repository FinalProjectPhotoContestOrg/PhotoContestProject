package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.services.contracts.ContestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String getHomeView() {
        return "HomeView";
    }
}
