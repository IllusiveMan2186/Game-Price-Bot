package com.gpb.backend.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocaleRequestDto {

    @NotBlank
    String locale;
}
