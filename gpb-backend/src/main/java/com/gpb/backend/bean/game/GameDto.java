package com.gpb.backend.bean.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameDto {

    private long id;

    private String name;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private boolean isAvailable;

    private List<Genre> genres;

    private ProductType type;

    private boolean isUserSubscribed;
}
