package com.gpb.web.bean.game;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameInfoDto extends GameDto {

    public GameInfoDto(Game game) {
        super(game);
        game.getGamesInShop().stream()
                .map(GameInStoreDto::new);
    }

    private List<GameInStoreDto> gamesInShop;
}
