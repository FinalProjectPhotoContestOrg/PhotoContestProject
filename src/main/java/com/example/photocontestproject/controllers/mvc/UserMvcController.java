package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.helpers.AuthenticationHelper;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller()
@RequestMapping("/users")
public class UserMvcController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;

    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String getUsersView(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
            if (user.getRole().equals(Role.Junkie)) {
                throw new AuthorizationException("You are not authorized to view users.");
            }
        } catch (AuthorizationException e) {
            session.setAttribute("redirectUrl", "/users");
            return "redirect:/login";
        }
        List<User> users = userService.getAllUsers(null, null, null);
        users.sort((u1, u2) -> u2.getPoints() - u1.getPoints());
        model.addAttribute("users", users);
        return "UsersView";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
            if (user.getRole().equals(Role.Junkie)) {
                return "redirect:/";
            }
        } catch (AuthorizationException e) {
            session.setAttribute("redirectUrl", "/users");
            return "redirect:/login";
        }


        userService.deleteUserById(id);
        return "redirect:/users";
    }
}
