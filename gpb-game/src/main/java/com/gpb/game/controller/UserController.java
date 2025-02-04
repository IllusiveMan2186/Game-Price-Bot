package com.gpb.game.controller;

import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling user-related operations.
 * <p>
 * This controller provides endpoints for:
 * <ul>
 *     <li>Creating an account linker token.</li>
 *     <li>Linking user accounts using a provided token.</li>
 *     <li>Creating a new user with notification preferences.</li>
 * </ul>
 * </p>
 */
@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a new {@code UserController} with the provided {@link UserService}.
     *
     * @param userService the service handling user business logic.
     */
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates an account linker token for the specified user.
     * <p>
     * The generated token can be used later to link user accounts.
     * </p>
     *
     * @param userId the unique identifier of the user for whom the token is generated.
     * @return a {@code String} representing the generated account linker token.
     */
    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public String createAccountLinkerToken(@RequestHeader(CommonConstants.BASIC_USER_ID_HEADER) final long userId) {
        log.info("Creating account linker token for user with id: {}", userId);
        return userService.createAccountLinkerToken(userId);
    }

    /**
     * Links a user accounts using the provided token.
     * <p>
     * This operation associates the account represented by the token with the user specified by the header.
     * </p>
     *
     * @param tokenDto the DTO containing the token used for linking accounts.
     * @param userId   the unique identifier of the user performing the account linking.
     * @return the unique identifier of the linked user account.
     */
    @PostMapping("/link")
    @ResponseStatus(HttpStatus.OK)
    public Long userAccountLink(@RequestBody final TokenRequestDto tokenDto,
                                @RequestHeader(CommonConstants.BASIC_USER_ID_HEADER) final long userId) {
        log.info("Linking account for user with id: {} using token: {}", userId, tokenDto.getToken());
        return userService.linkUsers(tokenDto.getToken(), userId).getId();
    }

    /**
     * Creates a new user with the specified notification preferences.
     * <p>
     * The {@code NotificationRequestDto} contains the notification type to be associated with the user.
     * </p>
     *
     * @param notificationRequestDto the DTO containing the user's notification type.
     * @return the unique identifier of the newly created user.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createUser(@RequestBody final NotificationRequestDto notificationRequestDto) {
        log.info("Creating new user with notification type: {}", notificationRequestDto.getUserNotificationType());
        return userService.createUser(notificationRequestDto.getUserNotificationType()).getId();
    }
}
