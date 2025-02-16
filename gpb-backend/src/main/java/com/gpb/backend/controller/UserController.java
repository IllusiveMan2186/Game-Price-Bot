package com.gpb.backend.controller;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.entity.dto.EmailRequestDto;
import com.gpb.backend.entity.dto.LocaleRequestDto;
import com.gpb.backend.entity.dto.PasswordChangeDto;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.GameService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.service.UserManagementService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final UserActivationService userActivationService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    public UserController(UserManagementService userManagementService,
                          UserAuthenticationService userAuthenticationService,
                          GameService gameService,
                          UserActivationService userActivationService,
                          UserAuthenticationProvider userAuthenticationProvider) {
        this.userManagementService = userManagementService;
        this.userAuthenticationService = userAuthenticationService;
        this.gameService = gameService;
        this.userActivationService = userActivationService;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }


    /**
     * Updates the email address for the authenticated user.
     *
     * <p>This endpoint allows a registered user to update their email by providing the new email address
     * in the request body. The user's email is updated using the authentication service, and a new token
     * is generated based on the updated email address.</p>
     *
     * @param emailRequestDto the DTO containing the new email information
     * @param user            the authenticated user's details
     * @return the updated {@link UserDto} after the email change
     */
    @PutMapping("/email")
    @Transactional
    public void updateUserEmail(@RequestBody final EmailRequestDto emailRequestDto, @AuthenticationPrincipal UserDto user) {
        log.info("User with ID {} requested email change", user.getId());

        userAuthenticationService.updateUserEmail(emailRequestDto.getEmail(), user);

        log.info("User with ID {} successfully updated email", user.getId());
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
     * @return the updated {@link UserDto} after the password change
     */
    @PutMapping("/password")
    @Transactional
    public UserDto updateUserPassword(@RequestBody final PasswordChangeDto passwordChangeDto, @AuthenticationPrincipal UserDto user) {
        log.info("User with ID {} requested password change", user.getId());

        UserDto updatedUser = userAuthenticationService.updateUserPassword(
                passwordChangeDto.getOldPassword(),
                passwordChangeDto.getNewPassword(),
                user);

        log.info("User with ID {} successfully changed password", user.getId());
        return updatedUser;
    }

    /**
     * Resend the activation email to the user
     *
     * @param emailRequestDto email for resending activation message
     */
    @PostMapping(value = "/resend/email")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void resendUserActivationEmail(@RequestBody final EmailRequestDto emailRequestDto) {
        log.info("Resending activation email for a user");

        userActivationService.resendActivationEmail(emailRequestDto.getEmail());

        log.info("Activation email resent successfully");
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
    @ResponseStatus(HttpStatus.OK)
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
    @ResponseStatus(HttpStatus.OK)
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
    @PutMapping("/locale")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void updateUserLocale(@RequestBody final LocaleRequestDto localeRequestDto,
                                 @AuthenticationPrincipal final UserDto user) {
        log.info("Updating locale to '{}' for user with ID {}", localeRequestDto.getLocale(), user.getId());
        userManagementService.updateLocale(localeRequestDto.getLocale(), user.getId());
    }
}
