package com.gpb.backend.bean.user.dto;

import lombok.Data;

import java.util.Locale;

@Data
public class EmailActivationResendDto {

    private String email;

    private Locale locale;
}
