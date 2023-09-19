package com.gpb.web.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class WebUser extends BasicUser {

    private String username;

    private String email;

    private String password;
}
