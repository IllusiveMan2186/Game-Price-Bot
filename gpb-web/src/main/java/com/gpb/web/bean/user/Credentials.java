package com.gpb.web.bean.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
public class Credentials {

    private String email;

    @ToString.Exclude
    private char[] password;
}
