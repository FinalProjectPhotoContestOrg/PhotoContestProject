package com.example.photocontestproject.helpers;
import com.example.photocontestproject.exceptions.AuthorizationException;
import com.example.photocontestproject.exceptions.EntityNotFoundException;
import com.example.photocontestproject.models.User;
import com.example.photocontestproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Component
public class AuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";


    private final UserService userService;
    @Autowired
    public AuthenticationHelper(UserService userService){


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
            if (!user.getPasswordHash().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
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
