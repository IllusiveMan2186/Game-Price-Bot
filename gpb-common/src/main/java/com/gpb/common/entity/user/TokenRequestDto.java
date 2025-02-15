package com.gpb.common.entity.user;

import com.gpb.common.util.CommonConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequestDto {

    @NotBlank
    @Pattern(regexp = CommonConstants.TOKEN_REGEX_PATTERN)
    private String token;
}
