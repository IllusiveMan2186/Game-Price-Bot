package com.gpb.web.bean.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
public class PasswordChangeDto {

    @ToString.Exclude
    private char[] password;
}
