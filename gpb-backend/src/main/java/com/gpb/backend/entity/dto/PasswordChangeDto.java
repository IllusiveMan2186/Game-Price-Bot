package com.gpb.backend.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
public class PasswordChangeDto {

    @ToString.Exclude
    private char[] password;
}
