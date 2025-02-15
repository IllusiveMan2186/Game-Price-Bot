package com.gpb.backend.entity.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.Locale;

@Data
public class EmailActivationResendDto {

    @NotBlank
    @Email
    private String email;

    private Locale locale;
}
