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
import com.gpb.backend.util.Constants;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.service.UserLinkerService;
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
     * Login user to system and return user info with token ,
     * also link with account if user went from messenger
     *
     * @param credentials credential for login
     * @param linkToken   token for likening
     * @return user info with token
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserDto login(@RequestBody Credentials credentials,
                         @RequestHeader(value = "LinkToken", required = false) String linkToken,
                         HttpServletResponse response) {
        log.info("User login attempt with email: {}", credentials.getEmail()); // Logging attempt

        UserDto userDto = userService.login(credentials);
        String token = userAuthenticationProvider.createToken(userDto.getEmail());

        if (credentials.isCookiesEnabled()) {
            response.addCookie(getAuthCookie(token));
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
     * Create new user
     *
     * @param user      user that would be registered in system
     * @param linkToken token for likening
     */
    @PostMapping(value = "/registration")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public void userRegistration(@RequestBody final UserRegistration user,
                                 @RequestHeader(value = "LinkToken", required = false) String linkToken) {
        WebUser webUser = userService.createUser(user);
        UserActivation activation = userActivationService.createUserActivation(webUser);
        emailService.sendEmailVerification(activation);
        linkAccounts(linkToken, webUser.getBasicUserId());
    }

    /**
     * Activate user
     *
     * @param tokenRequestDto token of user activation
     */
    @PostMapping(value = "/activate")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void userActivation(@RequestBody final TokenRequestDto tokenRequestDto) {
        userActivationService.activateUserAccount(tokenRequestDto.getToken());
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("User authenticated: {}", authentication.getPrincipal());
            return ResponseEntity.ok().build();
        }

        log.warn("User is NOT authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout-user")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletResponse response) {
        log.info("User logout request processing");
        Cookie cookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        log.info("User successfully logout");
    }

    private void linkAccounts(String linkToken, long currentUserBasicId) {
        log.info("Link token {} for user {}", linkToken, currentUserBasicId);

        if (linkToken != null) {
            userLinkerService.linkAccounts(linkToken, currentUserBasicId);
        }
    }

    private Cookie getAuthCookie(String token) {
        Cookie cookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.TOKEN_EXPIRATION);
        return cookie;
    }
}
