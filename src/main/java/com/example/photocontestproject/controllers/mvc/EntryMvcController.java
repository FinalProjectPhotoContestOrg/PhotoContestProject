package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.DuplicateEntityException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.RatingMapper;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.RatingService;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/entries")
public class EntryMvcController {

    private final EntryService entryService;
    private final AuthenticationHelper authenticationHelper;
    private final RatingMapper ratingMapper;
    private final RatingService ratingService;
    private final UserService userService;

    public EntryMvcController(EntryService entryService,
                              AuthenticationHelper authenticationHelper,
                              RatingMapper ratingMapper,
                              RatingService ratingService,
                              UserService userService) {
        this.entryService = entryService;
        this.authenticationHelper = authenticationHelper;
        this.ratingMapper = ratingMapper;
        this.ratingService = ratingService;
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public String getEntryView(BindingResult bindingResult,
                               @PathVariable int id,
                               HttpSession session,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "EntryView";
        }

        User user;
        Entry entry;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
            entry = entryService.getEntryById(id);
        } catch (AuthorizationException e) {
            session.setAttribute("redirectUrl", "/entries/" + id);
            return "redirect:/login";
        } catch (EntityNotFoundException e) {
            return "redirect:/";
        }

        String entryAvgScore = entryService.getAverageRating(entry);
        Set<Rating> ratings = entry.getRatings();
        boolean isJurorToContest = userService.isUserJurorToContest(user, entry);
        boolean alreadyRated = ratings.stream().anyMatch(rating -> rating.getJuror().getId().equals(user.getId()));
        int rank = entryService.getEntryRankInContest(entry);

        model.addAttribute("entryAvgScore", entryAvgScore);
        model.addAttribute("allRatings", ratings);
        model.addAttribute("entry", entry);
        model.addAttribute("ratingDto", new RatingDto());
        model.addAttribute("isOrganizer", user.getRole().equals(Role.Organizer));
        model.addAttribute("isJurorToContest", isJurorToContest);
        model.addAttribute("entryService", entryService);
        model.addAttribute("organizer", Role.Organizer);
        model.addAttribute("alreadyRated", alreadyRated);
        model.addAttribute("user", user);
        model.addAttribute("rank", rank);

        return "EntryView";
    }

    @PostMapping("/{id}")
    public String rateEntry(@ModelAttribute("ratingDto") RatingDto ratingDto,
                            BindingResult bindingResult,
                            @PathVariable int id,
                            HttpSession session,
                            Model model) {
        User user;
        Entry entry = null;
        try {
            entry = entryService.getEntryById(id);
        } catch (EntityNotFoundException e) {
            return "redirect:/";
        }
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
            Rating rating = ratingMapper.fromDto(ratingDto, entry, user.getId());
            ratingService.createRating(rating);
        } catch (AuthorizationException e) {
            return "redirect:/login";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("score", "duplicate_rating", e.getMessage());
            model.addAttribute("entry", entry);
            return "EntryView";
        }

        return "redirect:/";
    }
}
