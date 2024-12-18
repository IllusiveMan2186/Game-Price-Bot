package com.gpb.backend.controller;

import com.gpb.backend.bean.user.dto.UserDto;
import com.gpb.backend.service.UserLinkerService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/linker")
public class UserLinkerController {

    private final UserLinkerService userLinkerService;

    public UserLinkerController(UserLinkerService userLinkerService) {
        this.userLinkerService = userLinkerService;
    }

    /**
     * Connect telegram user to current web user
     *
     * @param token token that connected to telegram user
     * @param user  current user
     */
    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void connectTelegramUser(@RequestBody final String token,
                                    @AuthenticationPrincipal UserDto user) {
        userLinkerService.linkAccounts(token, user.getId());
    }

    /**
     * Get token for connect with telegram user
     *
     * @param user current user
     * @return token of connector
     */
    @GetMapping
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public String getTelegramUserConnectorToken(@AuthenticationPrincipal UserDto user) {
        return userLinkerService.getAccountsLinkerToken(user.getId());
    }
}
