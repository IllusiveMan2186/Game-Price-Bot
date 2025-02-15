package com.gpb.common.entity.game;

import com.gpb.common.util.CommonConstants;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddGameInStoreDto {

    @Min(1)
    private long gameId;

    @NotBlank
    @Pattern(regexp = CommonConstants.URL_REGEX_PATTERN)
    private String url;
}
