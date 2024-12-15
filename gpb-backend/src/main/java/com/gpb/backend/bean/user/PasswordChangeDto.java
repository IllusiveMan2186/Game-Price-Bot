package com.gpb.backend.bean.user;

import lombok.Data;
import lombok.ToString;

@Data
public class PasswordChangeDto {

    @ToString.Exclude
    private char[] password;
}
