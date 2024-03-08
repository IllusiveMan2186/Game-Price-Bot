package com.gpb.web.bean.user;

import lombok.Data;

import java.util.Locale;

@Data
public class EmailActivationResendDto {

    private String email;

    private Locale locale;
}
