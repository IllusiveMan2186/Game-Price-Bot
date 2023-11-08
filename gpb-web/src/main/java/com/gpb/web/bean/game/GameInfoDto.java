package com.gpb.web.bean.game;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameInfoDto extends GameDto {

    public GameInfoDto(Game game) {
        super(game);
        isUserSubscribed = false;
        gamesInShop = game.getGamesInShop().stream()
                .map(GameInStoreDto::new)
                .toList();
    }

    public GameInfoDto(Game game, boolean isUserSubscribed) {
        super(game);
        this.isUserSubscribed = isUserSubscribed;
        gamesInShop = game.getGamesInShop().stream()
                .map(GameInStoreDto::new)
                .toList();
    }

    private boolean isUserSubscribed;

    private List<GameInStoreDto> gamesInShop;
}
