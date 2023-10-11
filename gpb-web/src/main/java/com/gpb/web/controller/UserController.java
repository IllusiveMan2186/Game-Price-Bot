package com.gpb.web.controller;

import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextImpl;
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
     * @param session current user session
     * @return updated user
     */
    @PutMapping
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@RequestBody final UserRegistration newUser, HttpSession session) {
        long userId =getUserIdFromSession(session);
        return userService.updateUser(newUser, userId);
    }

    /**
     * Add game to user list of games
     *
     * @param gameId games id
     * @param session current user session
     * @return updated user
     */
    @PostMapping(value = "/games")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public UserDto addGameToUserListOfGames(@RequestBody final long gameId, HttpSession session) {
        long userId = getUserIdFromSession(session);
        userService.addGameToUserListOfGames(userId, gameId);
        return userService.getUserById(userId);
    }

    private long getUserIdFromSession(HttpSession session) {
        SecurityContextImpl securityContext = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");

        return ((UserDto) securityContext.getAuthentication().getPrincipal()).getId();
    }

}
