package com.gpb.backend.bean.user;

import lombok.Data;

import java.util.Locale;

@Data
public class EmailActivationResendDto {

    private String email;

    private Locale locale;
}
