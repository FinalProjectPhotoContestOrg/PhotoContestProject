package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.dtos.in.RatingDto;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.DuplicateEntityException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.RatingMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.Rating;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.RatingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.Set;

@Controller
@RequestMapping("/entries")
public class EntryMvcController {

    private final EntryService entryService;
    private final AuthenticationHelper authenticationHelper;
    private final RatingMapper ratingMapper;
    private final RatingService ratingService;

    public EntryMvcController(EntryService entryService,
                              AuthenticationHelper authenticationHelper,
                              RatingMapper ratingMapper,
                              RatingService ratingService) {
        this.entryService = entryService;
        this.authenticationHelper = authenticationHelper;
        this.ratingMapper = ratingMapper;
        this.ratingService = ratingService;
    }



    @GetMapping("/{id}")
    public String getEntryView(@ModelAttribute("entry") Entry entry,
                               BindingResult bindingResult,
                               @PathVariable int id,
                               HttpSession session,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "EntryView";
        }

        User user;
        Entry entry1;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
            entry1 = entryService.getEntryById(id);
        } catch (AuthorizationException e) {
            return "redirect:/login";
        } catch (EntityNotFoundException e) {
            return "redirect:/";
        }

        float entryAvgScore = (float) entry1.getEntryTotalScore() / entry1.getRatings().size();
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedScore = df.format(entryAvgScore);
        model.addAttribute("entryAvgScore", formattedScore);

        Set<Rating> ratings = entry1.getRatings();
        model.addAttribute("allRatings", ratings);

        model.addAttribute("entry", entry1);
        model.addAttribute("ratingDto", new RatingDto());

        Contest contest = entry1.getContest();
        model.addAttribute("isOrganizer", user.getRole().equals(Role.Organizer));
        model.addAttribute("isJurorToContest", contest.getJurors().stream().anyMatch(juror -> juror.getId().equals(user.getId())));
        return "EntryView";
    }

    @PostMapping("/{id}")
    public String rateEntry(@ModelAttribute("ratingDto")RatingDto ratingDto,
                            BindingResult bindingResult,
                            @PathVariable int id,
                            HttpSession session,
                            Model model) {
        User user;
        Entry entry = null;
        try {
            entry = entryService.getEntryById(id);
        }  catch (EntityNotFoundException e) {
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
