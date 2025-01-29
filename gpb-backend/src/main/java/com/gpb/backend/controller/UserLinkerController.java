package com.gpb.backend.controller;

import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.UserManagementService;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.service.UserLinkerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping("/linker")
public class UserLinkerController {

    private final UserLinkerService userLinkerService;
    private final UserManagementService userManagementService;

    /**
     * Connect telegram user to current web user
     *
     * @param token token that connected to another account
     * @param user  current user
     */
    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void linkUser(@RequestBody final TokenRequestDto token,
                         @AuthenticationPrincipal UserDto user) {
        userLinkerService.linkAccounts(token.getToken(), user.getId());
    }

    /**
     * Get token for connect with another account
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
