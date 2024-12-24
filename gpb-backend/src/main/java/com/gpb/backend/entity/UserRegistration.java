package com.gpb.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistration {

    private String email;

    @ToString.Exclude
    private char[] password;

    private String locale = "ua";
}
