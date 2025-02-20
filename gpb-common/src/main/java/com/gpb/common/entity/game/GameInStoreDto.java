package com.gpb.common.entity.game;

import com.gpb.common.util.CommonConstants;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameInStoreDto {

    @Min(1)
    private long id;

    @NotBlank
    private String nameInStore;

    @NotBlank
    @Pattern(regexp = CommonConstants.URL_REGEX_PATTERN)
    private String url;

    @DecimalMin("0.0")
    private BigDecimal price;

    @DecimalMin("0.0")
    private BigDecimal discountPrice;

    private boolean isAvailable;

    @Min(0)
    @Max(100)
    private int discount;

    @FutureOrPresent
    private Date discountDate;

    @NotNull
    private ClientActivationType clientType;
}
