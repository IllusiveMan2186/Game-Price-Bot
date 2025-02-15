package com.gpb.backend.controller;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.RefreshToken;
import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.RefreshTokenException;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.RefreshTokenService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import com.gpb.backend.util.CookieUtil;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.service.UserLinkerService;
import com.gpb.common.util.CommonConstants;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
    private final RefreshTokenService refreshTokenService;
    private final ModelMapper modelMapper;

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
    @Transactional
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserDto login(
            @RequestBody final Credentials credentials,

            @RequestHeader(value = "LinkToken", required = false)
            @Pattern(regexp = CommonConstants.TOKEN_REGEX_PATTERN) Optional<String> linkToken,

            final HttpServletResponse response
    ) {
        log.info("User login attempt");
        WebUser webUser = userService.login(credentials);

        String refreshToken = userAuthenticationProvider.generateRefreshToken(webUser);
        response.addCookie(createAuthCookie(refreshToken));

        String accessToken = userAuthenticationProvider.generateAccessToken(webUser.getId());

        linkToken.ifPresent(token -> linkAccounts(token, webUser.getBasicUserId()));
        log.info("User login successful: ID {}", webUser.getId());

        UserDto userDto = modelMapper.map(webUser, UserDto.class);
        userDto.setToken(accessToken);
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

                                 @RequestHeader(value = "LinkToken", required = false)
                                 @Nullable
                                 @Pattern(regexp = CommonConstants.TOKEN_REGEX_PATTERN) final String linkToken
    ) {
        log.info("Processing user registration request");
        WebUser webUser = userService.createUser(user);
        UserActivation activation = userActivationService.createUserActivation(webUser);
        emailService.sendEmailVerification(activation);
        linkAccounts(linkToken, webUser.getBasicUserId());
        log.info("User successfully registered");
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
        log.info("Processing user activation request");
        userActivationService.activateUserAccount(tokenRequestDto.getToken());
        log.info("User successfully activated");
    }

    /**
     * Checks if the current user is authenticated.
     *
     * @return a ResponseEntity with status 200 (OK) if the user is authenticated, or 401 (Unauthorized) otherwise
     */
    @PostMapping("/refresh-token")
    @Transactional
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Refresh user authentication token");
        String token = CookieUtil.getRefreshToken(request).orElseThrow(RefreshTokenException::new);

        RefreshToken refreshToken = refreshTokenService.getByToken(token).orElseThrow(RefreshTokenException::new);

        String newRefreshToken = userAuthenticationProvider.generateRefreshToken(refreshToken.getUser());
        response.addCookie(createAuthCookie(newRefreshToken));

        return userAuthenticationProvider.generateAccessToken(refreshToken.getUser().getId());
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
        SecurityContextHolder.clearContext();
        response.addCookie(invalidateAuthCookie());
        log.info("User successfully logged out");
    }

    /**
     * Links accounts if a link token is provided.
     *
     * @param linkToken          the token used for linking accounts (can be null)
     * @param currentUserBasicId the ID of the current user
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
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_COOKIES_ATTRIBUTE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.REFRESH_TOKEN_EXPIRATION / 1000);
        return cookie;
    }

    /**
     * Creates an invalidated authentication cookie for logout.
     */
    private Cookie invalidateAuthCookie() {
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_COOKIES_ATTRIBUTE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
