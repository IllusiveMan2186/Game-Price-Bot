package com.gpb.game.entity.game;

import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class GameRepositorySearchFilter {
    private List<Genre> genres;
    private List<ProductType> types;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long userId;
}
