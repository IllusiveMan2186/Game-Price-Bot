package com.gpb.game.bean.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameInfoDto extends GameDto {

    private List<GameInStoreDto> gamesInShop;
}
