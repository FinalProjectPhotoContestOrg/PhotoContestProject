package com.example.photocontestproject.helpers;

import com.example.photocontestproject.exceptions.AuthenticationFailureException;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.UserService;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class AuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";

    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        String userInfo = headers.getFirst(AUTHORIZATION_HEADER_NAME);
        if (userInfo == null || !userInfo.startsWith("Basic ")) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }

        try {
            String base64Credentials = userInfo.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }

            String username = values[0];
            String password = values[1];
            User user = userService.getByUsername(username);
            String hashedPassword = hashPassword(password);
            if (!user.getPasswordHash().equals(hashedPassword)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    public User tryGetCurrentUser(HttpSession session) {
        @Nullable Object currentUser = session.getAttribute("currentUser");

        if (!(currentUser instanceof User)) {
            throw new AuthorizationException("Invalid authentication. Please log in.");
        }
        return (User) currentUser;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public User throwIfWrongAuthentication(String username, String password) {
        User user = null;
        try {
            user = userService.getByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new AuthenticationFailureException("Wrong username or password.");
        }
        String passwordHash = hashPassword(password);
        if (!user.getPasswordHash().equals(passwordHash)) {
            throw new AuthenticationFailureException("Wrong username or password.");
        }
        return user;
    }
    /*public User tryGetUser(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()){
            if (authentication instanceof OAuth2AuthenticationToken){
                OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
                OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
                String username = oAuth2User.getAttribute("username");
                return userService.getByUsername(username);
            } else if (authentication instanceof UsernamePasswordAuthenticationToken){
                String username = authentication.getName();
                return userService.getByUsername(username);
            }
        }
        return null;
    }*/

}
