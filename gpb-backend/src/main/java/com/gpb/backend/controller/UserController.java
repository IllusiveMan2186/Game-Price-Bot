package com.gpb.backend.controller;

import com.gpb.backend.bean.user.dto.EmailRequestDto;
import com.gpb.backend.bean.user.dto.LocaleRequestDto;
import com.gpb.backend.bean.user.dto.PasswordChangeDto;
import com.gpb.backend.bean.user.dto.UserDto;
import com.gpb.backend.configuration.UserAuthenticationProvider;
import com.gpb.backend.service.GameService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.service.UserManagementService;
import jakarta.transaction.Transactional;
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
     * Update registered user email
     *
     * @param emailRequestDto new version of email
     * @param user  current user
     * @return updated user
     */
    @PutMapping("/email")
    @Transactional
    public UserDto updateUserEmail(@RequestBody final EmailRequestDto emailRequestDto, @AuthenticationPrincipal UserDto user) {
        UserDto userDto = userAuthenticationService.updateUserEmail(emailRequestDto.getEmail(), user);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        return userDto;
    }

    /**
     * Update registered user Password
     *
     * @param passwordChangeDto new version of password
     * @param user              current user
     * @return updated user
     */
    @PutMapping("/password")
    @Transactional
    public UserDto updateUserPassword(@RequestBody final PasswordChangeDto passwordChangeDto, @AuthenticationPrincipal UserDto user) {
        return userAuthenticationService.updateUserPassword(passwordChangeDto.getPassword(), user);
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
        userActivationService.resendActivationEmail(emailRequestDto.getEmail());
    }

    /**
     * Add game to user list of games
     *
     * @param gameId games id
     * @param user   current user
     * @return updated user
     */
    @PostMapping(value = "/games/{gameId}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void addGameToUserListOfGames(@PathVariable final long gameId, @AuthenticationPrincipal UserDto user) {
        gameService.setFollowGameOption(gameId, user.getId(), true);
    }

    /**
     * Add game to user list of games
     *
     * @param gameId games id
     * @param user   current user
     * @return updated user
     */
    @DeleteMapping(value = "/games/{gameId}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void removeGameFromUserListOfGames(@PathVariable final long gameId, @AuthenticationPrincipal UserDto user) {
        gameService.setFollowGameOption(gameId, user.getId(), false);
    }

    /**
     * Update user locale
     *
     * @param localeRequestDto new locale
     * @param user   user
     */
    @PutMapping("/locale")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void updateUserLocale(@RequestBody final LocaleRequestDto localeRequestDto,
                                 @AuthenticationPrincipal UserDto user) {
        userManagementService.updateLocale(localeRequestDto.getLocale(), user.getId());
    }
}
