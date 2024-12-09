package com.gpb.web.bean.game;

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

    private long id;

    private String nameInStore;

    private String url;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private boolean isAvailable;

    private int discount;

    private Date discountDate;

    private ClientActivationType clientType;
}
