package com.gpb.web.bean.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
public class UserRegistration {

    public UserRegistration(WebUser user){
        this.username = user.getEmail();
        this.password = user.getPassword().toCharArray();
    }

    private String username;

    @ToString.Exclude
    private char[] password;
}
