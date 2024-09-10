package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.dtos.ContestDto;
import com.example.photocontestproject.dtos.EntryDto;
import com.example.photocontestproject.dtos.in.ContestInDto;
import com.example.photocontestproject.enums.ContestPhase;
import com.example.photocontestproject.enums.ContestType;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.ContestMapper;
import com.example.photocontestproject.mappers.EntryMapper;
import com.example.photocontestproject.models.Contest;
import com.example.photocontestproject.models.Entry;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.ContestService;
import com.example.photocontestproject.services.contracts.EntryService;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/contests")
public class ContestMvcController {
    private final ContestService contestService;
    private final EntryService entryService;
    private final EntryMapper entryMapper;
    private final AuthenticationHelper authenticationHelper;
    private final ContestMapper contestMapper;
    private final UserService userService;

    public ContestMvcController(ContestService contestService, EntryService entryService, EntryMapper entryMapper, AuthenticationHelper authenticationHelper, ContestMapper contestMapper, UserService userService) {
        this.contestService = contestService;
        this.entryService = entryService;
        this.entryMapper = entryMapper;
        this.authenticationHelper = authenticationHelper;
        this.contestMapper = contestMapper;
        this.userService = userService;
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
            List<Entry> entries = contest.getEntries();
            List<Entry> sortedEntries = new ArrayList<>(entries);
            sortedEntries.sort(Comparator.comparing(Entry::getEntryTotalScore).reversed());
            Map<Integer, String> ranks = contestService.getRanks(sortedEntries);
            model.addAttribute("contest", contest);
            model.addAttribute("entry", new EntryDto());
            model.addAttribute("isOrganizer", user.getRole().equals(Role.Organizer));
            model.addAttribute("isPhaseI", contest.getContestPhase().equals(ContestPhase.PhaseI));
            model.addAttribute("isPhaseII", contest.getContestPhase().equals(ContestPhase.PhaseII));
            model.addAttribute("isFinished", contest.getContestPhase().equals(ContestPhase.Finished));
            model.addAttribute("isJuror", contest.getJurors().stream().anyMatch(juror -> juror.getId().equals(user.getId())));
            model.addAttribute("isInvited", contest.getParticipants().stream().anyMatch(participant -> participant.getId().equals(user.getId())));
            model.addAttribute("isInvitational", contest.getContestType().equals(ContestType.Invitational));
            model.addAttribute("entries", entries);
            model.addAttribute("sortedEntries", sortedEntries);
            model.addAttribute("ranks", ranks);
            return "ContestView";
        } catch (EntityNotFoundException e){
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("notFound", e.getMessage());
            return "ErrorView";
        }
    }
    @GetMapping("/create")
    public String getCreateContestView(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/login";
        }
        model.addAttribute("junkies", userService.getUsersByRole(Role.Junkie));
        model.addAttribute("masters", userService.getMasters());
        model.addAttribute("contest", new ContestDto());
        return "CreateContestView";
    }
    @PostMapping("/create")
    public String handleContestCreation(@RequestParam("coverPhoto") MultipartFile file,
                                        @ModelAttribute("contest") ContestDto contestDto,
                                        @RequestParam(value = "jurorIds", required = false) List<Integer> jurorIds,
                                        @RequestParam(value = "participantIds", required = false) List<Integer> participantIds,
                                        @RequestParam(value = "invitational", defaultValue = "false") boolean isInvitational,
                                        @SessionAttribute("currentUser") User user,
                                        Model model) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            contestDto.setCoverPhotoUrl(base64Image);
            Contest contest = contestMapper.fromDto(contestDto);
            contest.setOrganizer(user);
            contestService.createContest(contest, user);
            if (jurorIds == null) {
                jurorIds = List.of();
            }
            if (participantIds == null) {
                participantIds = List.of();
            }
            for (Integer jurorId : jurorIds) {
                contestService.addJuror(contest.getId(), jurorId, user);
            }
            for (Integer participantId : participantIds) {
                contestService.addParticipant(contest.getId(), participantId, user);
            }
            return "redirect:/";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/contests/create";
        } catch (AuthorizationException e){
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("notOrganizer", e.getMessage());
            return "ErrorView";
        }
    }
    @PostMapping("/{contestId}/entries")
    public String createEntry(@PathVariable Integer contestId,
                              @RequestParam("photo") MultipartFile photoFile,
                              @ModelAttribute("entry") EntryDto entryDto,
                              @SessionAttribute("currentUser") User user,
                              Model model) {
        try {
            String base64Image = Base64.getEncoder().encodeToString(photoFile.getBytes());
            entryDto.setPhotoUrl(base64Image);
            Entry entry = entryMapper.fromDto(entryDto, user);
            entry.setContest(contestService.getContestById(contestId));
            entryService.createEntry(entry, user);
            return "redirect:/";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("isOrganizer", e.getMessage());
            return "ErrorView"; // Ensure you have an ErrorView.html template for this.
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/contests/" + contestId;
        }
    }

}
