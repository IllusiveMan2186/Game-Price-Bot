package com.gpb.web.controller;

import com.gpb.web.bean.user.UserInfo;
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
     * Get user by id
     *
     * @param id users id
     * @return user
     */
    @GetMapping(value = "/info/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserInfo getUserById(@PathVariable final long id) {
        return new UserInfo(userService.getUserById(id));
    }

    /**
     * Update registered user
     *
     * @param newUser new version of user
     * @param session current user session
     * @return updated user
     */
    @PutMapping(value = "/info")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public UserInfo updateUser(@RequestBody final WebUser newUser, HttpSession session) {
        WebUser oldUser = getUserFromSession(session);
        return new UserInfo(userService.updateUser(newUser, oldUser));
    }

    /**
     * Add game to user list of games
     *
     * @param gameId games id
     * @param session current user session
     * @return updated user
     */
    @PostMapping(value = "/info/games")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public UserInfo addGameToUserListOfGames(@RequestBody final long gameId, HttpSession session) {
        WebUser user = getUserFromSession(session);
        userService.addGameToUserListOfGames(user.getId(), gameId);
        return new UserInfo(userService.getUserById(user.getId()));
    }

    /**
     * Create new user
     *
     * @param user user that would be registered in system
     * @return created user
     */
    @PostMapping(value = "/registration")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public UserInfo userRegistration(@RequestBody final WebUser user) {
        return new UserInfo(userService.createUser(user));
    }

    private WebUser getUserFromSession(HttpSession session) {
        SecurityContextImpl securityContext = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");

        return (WebUser) securityContext.getAuthentication().getPrincipal();
    }

}
