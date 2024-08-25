package com.example.photocontestproject.controllers.rest;

import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.services.contracts.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contests")
public class ContestController {
    private final ContestService contestService;
    @Autowired
    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }
    /*@PostMapping
    public Contest createContest(@RequestBody ContestDto contestDto){

    }*/
    //TODO Implement the class hahahha
}
