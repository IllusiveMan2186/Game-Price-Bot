package com.gpb.backend.controller;

import com.gpb.backend.entity.dto.UserDto;
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

/**
 * Controller for linking user accounts with external services such as Telegram.
 * <p>
 * Provides endpoints to link an external account using a token and to retrieve a token
 * for account linking.
 * </p>
 */
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/linker")
public class UserLinkerController {

    private final UserLinkerService userLinkerService;

    /**
     * Connects a Telegram user account to the current web user's account.
     *
     * @param token the DTO containing the token for account linking
     * @param user  the authenticated current user
     */
    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void linkUser(@RequestBody final TokenRequestDto token,
                         @AuthenticationPrincipal final UserDto user) {
        log.info("User with ID {} requested account linking", user.getId());
        userLinkerService.linkAccounts(token.getToken(), user.getId());
        log.info("User with ID {} successfully linked accounts", user.getId());
    }

    /**
     * Retrieves a token used for connecting with an external account (e.g., Telegram).
     *
     * <p>
     * The token is used to link the authenticated user's account with an external service.
     * </p>
     *
     * @param user the authenticated current user
     * @return the connector token as a {@link String}
     */
    @GetMapping
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String getTelegramUserConnectorToken(@AuthenticationPrincipal final UserDto user) {
        log.info("User with ID {} requested a connector token", user.getId());
        String token = userLinkerService.getAccountsLinkerToken(user.getId());
        log.info("User with ID {} received a connector token", user.getId());
        return token;
    }
}
