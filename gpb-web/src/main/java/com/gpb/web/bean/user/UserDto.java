package com.gpb.web.bean.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class UserDto extends User {

    private long id;

    private String email;

    private String token;

    private String locale;

    private long basicUserId;

    public UserDto() {
        super("", "", new ArrayList<>());
    }

    public UserDto(String username, String password, String token, String role, String locale) {
        super(username, password, Collections.singletonList(new SimpleGrantedAuthority(role)));
        eraseCredentials();
        this.email = username;
        this.token = token;
        this.locale = locale;
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

    public long getBasicUserId() {
        return basicUserId;
    }

    public void setBasicUserId(long basicUserId) {
        this.basicUserId = basicUserId;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserDto userDto = (UserDto) o;
        return id == userDto.id && Objects.equals(email, userDto.email) && Objects.equals(token, userDto.token)
                && Objects.equals(locale, userDto.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, email, token, locale);
    }
}
