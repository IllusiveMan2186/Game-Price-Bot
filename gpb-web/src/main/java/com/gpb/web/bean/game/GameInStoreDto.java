package com.gpb.web.bean.game;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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
    }

    private long id;

    private String nameInStore;

    private String url;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private boolean isAvailable;

    private int discount;

    private Date discountDate;
}
