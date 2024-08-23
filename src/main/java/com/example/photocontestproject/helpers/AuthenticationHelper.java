package com.example.photocontestproject.helpers;

/*
@Component
public class AuthenticationHelper {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";
    private final UserService userService;
    @Autowired
    public AuthenticationHelper(UserService userService){

        this.userService = userService;
    }
    public User tryGetUser(HttpHeaders headers){
        String userInfo = headers.getFirst(AUTHORIZATION_HEADER_NAME);
        if (userInfo == null || !userInfo.startsWith("Basic ")){
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
            Optional<User> userOptional = userService.getByUsername(username);
            User user = userOptional.orElseThrow(() -> new AuthorizationException(INVALID_AUTHENTICATION_ERROR));
            if (!user.getPasswordHash().equals(password)) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            throwIfUserIsBlocked(user);
            return user;
        }
        return null;
    }
}
*/
