package com.gpb.web.controller;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.user.EmailChangeDto;
import com.gpb.web.bean.user.PasswordChangeDto;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.configuration.UserAuthenticationProvider;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.service.UserActivationService;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final GameStoresService storesService;
    private final UserActivationService userActivationService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    public UserController(UserService userService, GameStoresService storesService,
                          GameService gameService, UserActivationService userActivationService, UserAuthenticationProvider userAuthenticationProvider) {
        this.userService = userService;
        this.gameService = gameService;
        this.storesService = storesService;
        this.userActivationService = userActivationService;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }


    /**
     * Update registered user email
     *
     * @param emailChangeDto new version of email
     * @param user           current user
     * @return updated user
     */
    @PutMapping("/email")
    @Transactional
    public UserDto updateUserLocale(@RequestBody final EmailChangeDto emailChangeDto, @AuthenticationPrincipal UserDto user) {
        UserDto userDto = userService.updateUserEmail(emailChangeDto.getEmail(), user);
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
        userService.subscribeToGame(user.getId(), gameId);
        Game game = gameService.getById(gameId);
        if (!game.isFollowed()) {
            game.setFollowed(true);
            storesService.subscribeToGame(gameId);
        }
        return userService.getUserById(user.getId());
    }

    /**
     * Resend the activation email to the user
     *
     * @param email user email
     */
    @PostMapping(value = "/resend/email/{email}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public void resendUserActivationEmail(@PathVariable final String email) {
        userActivationService.resendActivationEmail(email);
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
        userService.unsubscribeFromGame(user.getId(), gameId);
        Game game = gameService.getById(gameId);
        if (game.isFollowed() && game.getUserList().size() < 1) {
            storesService.unsubscribeFromGame(gameId);
        }
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
    public void updateUserLocale(@PathVariable final String locale, @AuthenticationPrincipal UserDto user) {
        userService.updateLocale(locale, user.getId());
    }
}
