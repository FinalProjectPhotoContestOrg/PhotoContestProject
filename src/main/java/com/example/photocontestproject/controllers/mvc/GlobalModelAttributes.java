package com.example.photocontestproject.controllers.mvc;

import com.example.photocontestproject.enums.Role;
import com.example.photocontestproject.models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("isOrganizer")
    public boolean populateIsOrganizer(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null && user.getRole().equals(Role.Organizer);
    }
}
