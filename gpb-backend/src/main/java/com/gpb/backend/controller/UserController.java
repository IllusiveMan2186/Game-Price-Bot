package com.gpb.backend.controller;

import com.gpb.backend.entity.dto.LocaleRequestDto;
import com.gpb.backend.entity.dto.PasswordChangeDto;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.GameService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.service.UserManagementService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user-related operations .
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserManagementService userManagementService;
    private final UserAuthenticationService userAuthenticationService;
    private final GameService gameService;

    public UserController(UserManagementService userManagementService,
                          UserAuthenticationService userAuthenticationService,
                          GameService gameService) {
        this.userManagementService = userManagementService;
        this.userAuthenticationService = userAuthenticationService;
        this.gameService = gameService;
    }

    /**
     * Updates the password for the authenticated user.
     *
     * <p>
     * This endpoint allows a registered user to update their password by providing the new password details in the request body.
     * The updated user details are returned upon successful password change.
     * </p>
     *
     * @param passwordChangeDto the DTO containing the new password information
     * @param user              the authenticated user's details
     */
    @PatchMapping("/password")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserPassword(@RequestBody final PasswordChangeDto passwordChangeDto,
                                      @AuthenticationPrincipal UserDto user) {
        log.info("User with ID {} requested password change", user.getId());

        userAuthenticationService.updateUserPassword(
                passwordChangeDto.getOldPassword(),
                passwordChangeDto.getNewPassword(),
                user);

        log.debug("User with ID {} successfully changed password", user.getId());
    }

    /**
     * Adds a game to the authenticated user's list of followed games.
     *
     * <p>
     * This endpoint allows the current user to add a game (specified by its ID) to their list of followed games.
     * The game is added by setting the follow option to {@code true}.
     * </p>
     *
     * @param gameId the ID of the game to add
     * @param user   the authenticated user's details
     */
    @PostMapping(value = "/games/{gameId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addGameToUserListOfGames(
            @PathVariable
            @Positive final long gameId,

            @AuthenticationPrincipal final UserDto user
    ) {
        log.info("Adding game {} to the list of followed games for user {}", gameId, user.getId());
        gameService.setFollowGameOption(gameId, user.getBasicUserId(), true);
    }

    /**
     * Removes a game from the authenticated user's list of followed games.
     *
     * <p>
     * This endpoint allows the current user to remove a game (specified by its ID) from their list of followed games.
     * The game is removed by setting the follow option to {@code false}.
     * </p>
     *
     * @param gameId the ID of the game to remove
     * @param user   the authenticated user's details
     */
    @DeleteMapping(value = "/games/{gameId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeGameFromUserListOfGames(
            @PathVariable
            @Positive final long gameId,

            @AuthenticationPrincipal final UserDto user
    ) {
        log.info("Removing game {} from the list of followed games for user {}", gameId, user.getId());
        gameService.setFollowGameOption(gameId, user.getBasicUserId(), false);
    }

    /**
     * Update the locale for the authenticated user.
     *
     * <p>
     * This endpoint allows an authenticated user to update their locale setting.
     * </p>
     *
     * @param localeRequestDto the DTO containing the new locale value
     * @param user             the authenticated user's details
     */
    @PatchMapping("/locale")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserLocale(@RequestBody final LocaleRequestDto localeRequestDto,
                                 @AuthenticationPrincipal final UserDto user) {
        log.info("Updating locale to '{}' for user with ID {}", localeRequestDto.getLocale(), user.getId());
        userManagementService.updateLocale(localeRequestDto.getLocale(), user.getId());
    }
}
