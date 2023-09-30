package com.gpb.web.controller;

import com.gpb.web.bean.UserInfo;
import com.gpb.web.bean.WebUser;
import com.gpb.web.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextImpl;
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

    @GetMapping(value = "/info/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserInfo getUserById(@PathVariable final long id) {
        return new UserInfo(userService.getUserById(id));
    }

    @PostMapping(value = "/info")
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public UserInfo updateUser(@RequestBody final WebUser newUser, HttpSession session) {
        WebUser oldUser = getUserFromSession(session);
        return new UserInfo(userService.updateUser(newUser, oldUser));
    }

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
