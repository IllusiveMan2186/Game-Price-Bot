package com.gpb.web.bean.game;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Data
public class GameDto {
    
    public GameDto(){
    }

    public GameDto(Game game){
        this.id = game.getId();
        this.name = game.getName();
        this.genres = game.getGenres();
        this.isAvailable = game.getGamesInShop().stream().anyMatch(GameInShop::isAvailable);
        this.minPrice = game.getGamesInShop().stream()
                .map(GameInShop::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(null);
        this.maxPrice = game.getGamesInShop().stream()
                .map(GameInShop::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private long id;

    private String name;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private boolean isAvailable;

    private List<Genre> genres;
}
