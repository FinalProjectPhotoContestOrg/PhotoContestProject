package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.EntryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/entries")
public class EntryMvcController {

    private final EntryService entryService;
    private final AuthenticationHelper authenticationHelper;

    public EntryMvcController(EntryService entryService, AuthenticationHelper authenticationHelper) {
        this.entryService = entryService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/{id}")
    public String getEntryView(@PathVariable int id, HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/login";
        }

        Entry entry = entryService.getEntryById(id);
        model.addAttribute("entry", entry);
        return "EntryView";
    }
}
