package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.dtos.in.EmailDto;
import com.example.photocontestproject.dtos.in.PasswordDto;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.external.service.EmailService;
import com.example.photocontestproject.mappers.UserMapper;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/reset-password")
public class ResetPasswordMvcController {
    private final UserService userService;
    private final EmailService emailService;
    private final UserMapper userMapper;

    public ResetPasswordMvcController(UserService userService, EmailService emailService, UserMapper userMapper) {
        this.userService = userService;
        this.emailService = emailService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String getResetPasswordView(Model model) {
        model.addAttribute("email", new EmailDto());
        return "ResetPasswordView";
    }


    @PostMapping()
    public String handleSendResetPasswordEmail(@ModelAttribute("email") EmailDto email, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "ResetPasswordView";
        }
        User user;
        try {
            user = userService.getUserByEmail(email.getEmail());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "ResetPasswordView";
        }
        String encodedPasswordHash = URLEncoder.encode(user.getPasswordHash(), StandardCharsets.UTF_8);
        String resetLink = String.format("http://localhost:8080/reset-password/form?userId=%d&hash=%s",
                user.getId(),
                encodedPasswordHash);
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
        return "redirect:/login";
    }

    @GetMapping("/form")
    public String getResetPasswordForm(@RequestParam("userId") int id,
                                       @RequestParam("hash") String encodedPasswordHash,
                                       Model model) {
        User user;
        try {
            user = userService.getUserById(id);
            String decodedPasswordHash = URLDecoder.decode(encodedPasswordHash, StandardCharsets.UTF_8);
            if (!user.getPasswordHash().equals(decodedPasswordHash)) {
                return "redirect:/";
            }
        } catch (EntityNotFoundException e) {
            return "redirect:/";
        }
        String resetLink = String.format("http://localhost:8080/reset-password/form?userId=%d&hash=%s",
                user.getId(),
                encodedPasswordHash);
        model.addAttribute("url", resetLink);
        model.addAttribute("passwordDto", new PasswordDto());
        return "NewPasswordView";
    }

    @PostMapping("/form")
    public String handlePasswordChange(@ModelAttribute("passwordDto") PasswordDto passwordDto,
                                       BindingResult bindingResult,
                                       @RequestParam("userId") int id,
                                       @RequestParam("hash") String encodedPasswordHash) {
        if (bindingResult.hasErrors()) {
            return "redirect:/";
        }
        User user;
        try {
            user = userService.getUserById(id);
            String decodedPasswordHash = URLDecoder.decode(encodedPasswordHash, StandardCharsets.UTF_8);
            if (!user.getPasswordHash().equals(decodedPasswordHash)) {
                return "redirect:/";
            }
        } catch (EntityNotFoundException e) {
            return "redirect:/";
        }
        user = userMapper.updatePassword(user, passwordDto);
        userService.updateUser(user);
        return "redirect:/login";
    }
}
