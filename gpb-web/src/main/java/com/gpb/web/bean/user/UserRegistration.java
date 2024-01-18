package com.gpb.web.bean.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
public class UserRegistration {

    public UserRegistration(WebUser user){
        this.email = user.getEmail();
        this.password = user.getPassword().toCharArray();
        this.locale = user.getLocale().getLanguage();
    }

    private String email;

    @ToString.Exclude
    private char[] password;

    private String locale = "ua";
}
