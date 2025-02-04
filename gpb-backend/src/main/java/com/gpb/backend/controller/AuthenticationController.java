package com.gpb.backend.controller;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.common.service.UserLinkerService;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.backend.util.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user authentication operations.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final UserAuthenticationService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final UserActivationService userActivationService;
    private final UserLinkerService userLinkerService;
    private final EmailService emailService;

    /**
     * Authenticates a user with the provided credentials and returns user information along with an authentication token.
     * If cookies are enabled, the token is stored in a secure HTTP-only cookie.
     * Optionally links accounts if a link token is provided.
     *
     * @param credentials the user credentials for login
     * @param linkToken   an optional token for linking accounts (from messenger, etc.)
     * @param response    the HTTP response used to add authentication cookies if enabled
     * @return the authenticated user's information (token is omitted if cookies are used)
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserDto login(@RequestBody final Credentials credentials,
                         @RequestHeader(value = "LinkToken", required = false) final String linkToken,
                         final HttpServletResponse response) {
        log.info("User login attempt with email: {}", credentials.getEmail());
        UserDto userDto = userService.login(credentials);
        String token = userAuthenticationProvider.createToken(userDto.getEmail());

        if (credentials.isCookiesEnabled()) {
            response.addCookie(createAuthCookie(token));
            userDto.setToken(null);
            log.info("User ID {} logged in with cookies enabled", userDto.getBasicUserId());
        } else {
            userDto.setToken(token);
            log.info("User ID {} logged in with token-based authentication", userDto.getBasicUserId());
        }

        linkAccounts(linkToken, userDto.getBasicUserId());
        log.info("User ID {} linked accounts if applicable", userDto.getBasicUserId());
        return userDto;
    }

    /**
     * Registers a new user and sends an activation email.
     * Optionally links accounts if a link token is provided.
     *
     * @param user      the registration details of the new user
     * @param linkToken an optional token for linking accounts
     */
    @PostMapping("/registration")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public void userRegistration(@RequestBody final UserRegistration user,
                                 @RequestHeader(value = "LinkToken", required = false) final String linkToken) {
        WebUser webUser = userService.createUser(user);
        UserActivation activation = userActivationService.createUserActivation(webUser);
        emailService.sendEmailVerification(activation);
        linkAccounts(linkToken, webUser.getBasicUserId());
    }

    /**
     * Activates a user's account using the provided activation token.
     *
     * @param tokenRequestDto the DTO containing the activation token
     */
    @PostMapping("/activate")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void userActivation(@RequestBody final TokenRequestDto tokenRequestDto) {
        userActivationService.activateUserAccount(tokenRequestDto.getToken());
    }

    /**
     * Checks if the current user is authenticated.
     *
     * @return a ResponseEntity with status 200 (OK) if the user is authenticated, or 401 (Unauthorized) otherwise
     */
    @GetMapping("/check-auth")
    public ResponseEntity<Void> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("User authenticated: {}", authentication.getPrincipal());
            return ResponseEntity.ok().build();
        }
        log.warn("User is NOT authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Logs out the current user by invalidating the authentication token cookie.
     *
     * @param response the HTTP response used to remove the authentication cookie
     */
    @PostMapping("/logout-user")
    @ResponseStatus(HttpStatus.OK)
    public void logout(final HttpServletResponse response) {
        log.info("Processing user logout request");
        Cookie cookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Invalidate cookie immediately
        response.addCookie(cookie);
        log.info("User successfully logged out");
    }

    /**
     * Links accounts if a link token is provided.
     *
     * @param linkToken            the token used for linking accounts (can be null)
     * @param currentUserBasicId   the ID of the current user
     */
    private void linkAccounts(final String linkToken, final long currentUserBasicId) {
        log.info("Link token {} for user {}", linkToken, currentUserBasicId);
        if (linkToken != null) {
            userLinkerService.linkAccounts(linkToken, currentUserBasicId);
        }
    }

    /**
     * Creates an authentication cookie with the specified token.
     *
     * @param token the authentication token
     * @return the configured authentication Cookie
     */
    private Cookie createAuthCookie(final String token) {
        Cookie cookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.TOKEN_EXPIRATION);
        return cookie;
    }
}
