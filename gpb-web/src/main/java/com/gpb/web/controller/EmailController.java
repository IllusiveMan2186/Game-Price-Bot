package com.gpb.web.controller;

import com.gpb.web.service.UserActivationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/email")
public class EmailController {

    private final UserActivationService userActivationService;

    public EmailController(UserActivationService userActivationService) {
        this.userActivationService = userActivationService;
    }

    /**
     * Activate user
     *
     * @param token token of user activation
     */
    @GetMapping(value = "/{token}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void userActivation(@PathVariable final String token) {
        userActivationService.activateUserAccount(token);
    }
}
