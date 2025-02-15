package com.gpb.common.entity.game;

import com.gpb.common.util.CommonConstants;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameDto {


    @Min(1)
    private long id;

    @NotBlank
    @Pattern(regexp = CommonConstants.NAME_REGEX_PATTERN)
    private String name;

    @DecimalMin("0.0")
    private BigDecimal minPrice;

    @DecimalMin("0.0")
    private BigDecimal maxPrice;

    private boolean isAvailable;

    @NotEmpty
    private List<Genre> genres;

    @NotNull
    private ProductType type;

    private boolean isUserSubscribed;
}
