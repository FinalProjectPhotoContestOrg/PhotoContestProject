package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.dtos.in.LogInDto;
import com.example.photocontestproject.dtos.in.RegisterDto;
import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthenticationFailureException;
import com.example.photocontestproject.exceptions.DuplicateEntityException;
import com.example.photocontestproject.exceptions.EmailException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.mappers.UserMapper;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthMvcController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public AuthMvcController(UserMapper userMapper, UserService userService, AuthenticationHelper authenticationHelper) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/login")
    public String getLoginView(Model model) {
        model.addAttribute("login", new LogInDto());
        return "LoginView";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LogInDto loginDto,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "LoginView";
        }

        try {
            User user = authenticationHelper.throwIfWrongAuthentication(loginDto.getUsername(),
                    loginDto.getPassword());
            session.setAttribute("currentUser", user);
            session.setAttribute("userId", user.getId());
            String redirectUrl = (String) session.getAttribute("redirectUrl");
            session.removeAttribute("redirectAfterLogin");
            if (redirectUrl == null) {
                redirectUrl = "/";
            }
            if (redirectUrl.equalsIgnoreCase("/dashboard")) {
                if (user.getRole().equals(Role.Organizer)) {
                    return "redirect:/dashboard/organizer";
                } else {
                    return "redirect:/dashboard/junkies";
                }
            }

            return "redirect:" + redirectUrl;
        } catch (AuthenticationFailureException e) {
            bindingResult.rejectValue("username", "auth_error", e.getMessage());
            return "LoginView";
        }
    }

    @GetMapping("/register")
    public String getRegisterView(Model model) {
        model.addAttribute("register", new RegisterDto());
        return "RegisterView";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") RegisterDto registerDto, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "RegisterView";
        }
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            bindingResult.rejectValue("password", "confirm_password_error", "Passwords should match.");
            return "RegisterView";
        }
        try {
            User user = userMapper.fromDto(registerDto);
            userService.createUser(user);
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("username", "duplicate_user", e.getMessage());
            return "RegisterView";
        } catch (EmailException e) {
            bindingResult.rejectValue("email", "email_error", e.getMessage());
            return "RegisterView";
        }
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/";
    }

}
