package com.gpb.web.bean.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collections;

public class UserDto extends User {

    private long id;

    private String email;

    private String token;

    private String locale;

    public UserDto() {
        super("", "", new ArrayList<>());
    }

    public UserDto(String username, String password, String token, String role) {
        super(username, password, Collections.singletonList(new SimpleGrantedAuthority(role)));
        eraseCredentials();
        this.email = username;
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return super.getPassword();
    }
}
