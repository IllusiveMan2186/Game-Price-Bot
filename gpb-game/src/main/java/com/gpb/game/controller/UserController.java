package com.gpb.game.controller;

import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.util.CommonConstants;
import com.gpb.game.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public String createAccountLinkerToken(@RequestHeader(CommonConstants.BASIC_USER_ID_HEADER) long userId) {
        return userService.createAccountLinkerToken(userId);
    }

    @PostMapping("/link")
    @ResponseStatus(HttpStatus.OK)
    public Long userAccountLink(@RequestBody final TokenRequestDto token,
                                @RequestHeader(CommonConstants.BASIC_USER_ID_HEADER) long userId) {
        return userService.linkUsers(token.getToken(), userId).getId();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createUser(@RequestBody final NotificationRequestDto notificationRequestDto) {
        return userService.createUser(notificationRequestDto.getUserNotificationType()).getId();
    }
}
