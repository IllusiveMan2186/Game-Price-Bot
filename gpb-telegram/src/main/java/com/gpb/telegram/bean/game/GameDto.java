package com.gpb.telegram.bean.game;

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

    private long id;

    private String name;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private boolean isAvailable;

    private List<Genre> genres;

    private ProductType type;

    private boolean isUserSubscribed;
}
