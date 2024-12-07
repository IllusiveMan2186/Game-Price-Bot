package com.gpb.stores.bean.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameInfoDto extends GameDto {

    private boolean isUserSubscribed;

    private List<GameInStoreDto> gamesInShop;
}
