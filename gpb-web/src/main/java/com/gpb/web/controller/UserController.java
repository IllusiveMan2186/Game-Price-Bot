package com.gpb.web.controller;

import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.UserDto;
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

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Update registered user
     *
     * @param newUser new version of user
     * @param user current user
     * @return updated user
     */
    @PutMapping
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody final UserRegistration newUser, @AuthenticationPrincipal UserDto user) {
        return userService.updateUser(newUser, user.getId());
    }

    /**
     * Add game to user list of games
     *
     * @param gameId games id
     * @param user current user
     * @return updated user
     */
    @PostMapping(value = "/games/{gameId}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public UserDto addGameToUserListOfGames(@PathVariable final long gameId, @AuthenticationPrincipal UserDto user ) {
        userService.subscribeToGame(user.getId(), gameId);
        return userService.getUserById(user.getId());
    }
}
