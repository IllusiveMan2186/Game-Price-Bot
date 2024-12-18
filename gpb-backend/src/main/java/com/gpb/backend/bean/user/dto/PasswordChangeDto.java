package com.gpb.backend.bean.user.dto;

import lombok.Data;
import lombok.ToString;

@Data
public class PasswordChangeDto {

    @ToString.Exclude
    private char[] password;
}
