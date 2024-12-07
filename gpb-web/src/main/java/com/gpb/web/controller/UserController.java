package com.gpb.web.controller;

import com.gpb.web.bean.user.PasswordChangeDto;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.configuration.UserAuthenticationProvider;
import com.gpb.web.service.GameService;
import com.gpb.web.service.UserActivationService;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final GameService gameService;
    private final UserActivationService userActivationService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    public UserController(UserService userService,
                          GameService gameService,
                          UserActivationService userActivationService,
                          UserAuthenticationProvider userAuthenticationProvider) {
        this.userService = userService;
        this.gameService = gameService;
        this.userActivationService = userActivationService;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }


    /**
     * Update registered user email
     *
     * @param email new version of email
     * @param user  current user
     * @return updated user
     */
    @PutMapping("/email")
    @Transactional
    public UserDto updateUserEmail(@RequestBody final String email, @AuthenticationPrincipal UserDto user) {
        UserDto userDto = userService.updateUserEmail(email, user);
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
        return userService.updateUserPassword(passwordChangeDto.getPassword(), user);
    }

    /**
     * Resend the activation email to the user
     *
     * @param email email for resending activation message
     */
    @PostMapping(value = "/resend/email")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void resendUserActivationEmail(@RequestBody final String email) {
        userActivationService.resendActivationEmail(email);
    }

    /**
     * Connect telegram user to current web user
     *
     * @param token token that connected to telegram user
     * @param user  current user
     */
    @PostMapping(value = "/connect/telegram/{token}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void connectTelegramUser(@PathVariable final String token, @AuthenticationPrincipal UserDto user) {
        userService.connectTelegramUser(token, user.getId());
    }

    /**
     * Get token for connect with telegram user
     *
     * @param user current user
     * @return token of connector
     */
    @GetMapping(value = "/connect/telegram")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public String getTelegramUserConnectorToken(@AuthenticationPrincipal UserDto user) {
        return userService.getTelegramUserConnectorToken(user.getId());
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
    public UserDto addGameToUserListOfGames(@PathVariable final long gameId, @AuthenticationPrincipal UserDto user) {
        gameService.setFollowGameOption(user.getId(), gameId, true);
        return userService.getUserById(user.getId());
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
    public UserDto removeGameFromUserListOfGames(@PathVariable final long gameId, @AuthenticationPrincipal UserDto user) {
        gameService.setFollowGameOption(user.getId(), gameId, false);
        return userService.getUserById(user.getId());
    }

    /**
     * Update user locale
     *
     * @param locale new locale
     * @param user   user
     */
    @PutMapping("/locale/{locale}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void updateUserLocale(@RequestBody final String locale,
                                 @AuthenticationPrincipal UserDto user) {
        userService.updateLocale(locale, user.getId());
    }
}
