package com.gpb.web.controller;

import com.gpb.web.bean.WebUser;
import com.gpb.web.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/id")
    public WebUser getUserById(@RequestParam final long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping(value = "/email")
    public WebUser getUserByEmail(@RequestParam final String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping(value = "/username")
    public WebUser getUserByUsername(@RequestParam final String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping
    @Transactional
    public WebUser createUser(@RequestParam final WebUser user) {
        return userService.createUser(user);
    }

    @DeleteMapping
    @Transactional
    public boolean removeUser(@RequestParam final long userId) {
        return userService.deleteUser(userId);
    }
}
