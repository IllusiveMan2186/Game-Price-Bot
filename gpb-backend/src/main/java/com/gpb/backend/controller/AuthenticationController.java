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
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
     * @param linkToken token for likening
     * @return user info with token
     */
    @PostMapping("/login")
    public UserDto login(@RequestBody Credentials credentials,
                         @RequestHeader(value = "LinkToken", required = false) String linkToken) {
        UserDto userDto = userService.login(credentials);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        linkAccounts(linkToken, userDto.getBasicUserId());
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

    private void linkAccounts(String linkToken, long currentUserBasicId) {
        log.info("Link token {} for user {}", linkToken, currentUserBasicId);

        if (linkToken != null) {
            userLinkerService.linkAccounts(linkToken, currentUserBasicId);
        }
    }
}
