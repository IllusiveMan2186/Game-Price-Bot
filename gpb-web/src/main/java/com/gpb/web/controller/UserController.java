package com.gpb.web.controller;

import com.gpb.web.bean.user.EmailChangeDto;
import com.gpb.web.bean.user.PasswordChangeDto;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.configuration.UserAuthenticationProvider;
import com.gpb.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final UserAuthenticationProvider userAuthenticationProvider;

    public UserController(UserService userService, UserAuthenticationProvider userAuthenticationProvider) {
        this.userService = userService;
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
    public UserDto updateUserEmail(@RequestBody final EmailChangeDto emailChangeDto, @AuthenticationPrincipal UserDto user) {
        UserDto userDto =  userService.updateUserEmail(emailChangeDto.getEmail(), user);
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
        return userService.getUserById(user.getId());
    }
}
