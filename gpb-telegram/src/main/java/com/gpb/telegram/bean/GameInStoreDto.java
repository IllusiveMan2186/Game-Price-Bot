package com.gpb.telegram.bean;

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

    public GameInStoreDto(GameInShop gameInShop){
        this.id = gameInShop.getId();
        this.nameInStore = gameInShop.getNameInStore();
        this.url = gameInShop.getUrl();
        this.price = gameInShop.getPrice();
        this.isAvailable = gameInShop.isAvailable();
        this.discount = gameInShop.getDiscount();
        this.discountDate = gameInShop.getDiscountDate();
        this.discountPrice = gameInShop.getDiscountPrice();
        this.clientType = gameInShop.getClientType();
    }

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
