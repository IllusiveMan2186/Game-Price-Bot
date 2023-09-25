package com.gpb.web.controller;

import com.gpb.web.bean.WebUser;
import com.gpb.web.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WebUser getUserById(@PathVariable final long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public WebUser createUser(@RequestBody final WebUser user) {
        return userService.createUser(user);
    }

    @DeleteMapping(value = "/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public boolean removeUser(@PathVariable final long id) {
        return userService.deleteUser(id);
    }
}
