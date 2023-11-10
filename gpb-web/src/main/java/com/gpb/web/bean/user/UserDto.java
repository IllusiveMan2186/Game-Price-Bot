package com.gpb.web.bean.user;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    public UserDto(WebUser user) {
        this.email = user.getEmail();
        this.id = user.getId();
    }

    private long id;

    private String email;

    private String token;
}
