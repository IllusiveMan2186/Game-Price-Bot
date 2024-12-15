package com.gpb.backend.controller;

import com.gpb.backend.bean.user.Credentials;
import com.gpb.backend.bean.user.UserActivation;
import com.gpb.backend.bean.user.UserDto;
import com.gpb.backend.bean.user.UserRegistration;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.configuration.UserAuthenticationProvider;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final UserAuthenticationService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final UserActivationService userActivationService;
    private final EmailService emailService;

    public AuthenticationController(UserAuthenticationService userService, UserAuthenticationProvider userAuthenticationProvider,
                                    UserActivationService userActivationService, EmailService emailService) {
        this.userService = userService;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.userActivationService = userActivationService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public UserDto login(@RequestBody Credentials credentials, HttpServletRequest request) {
        UserDto userDto = userService.login(credentials);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        return userDto;
    }

    /**
     * Create new user
     *
     * @param user user that would be registered in system
     */
    @PostMapping(value = "/registration")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public void userRegistration(@RequestBody final UserRegistration user) {
        WebUser webUser = userService.createUser(user);
        UserActivation activation = userActivationService.createUserActivation(webUser);
        emailService.sendEmailVerification(activation);
    }
}
