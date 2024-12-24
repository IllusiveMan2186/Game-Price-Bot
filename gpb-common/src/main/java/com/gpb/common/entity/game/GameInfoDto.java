package com.gpb.common.entity.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameInfoDto extends GameDto {

    private List<GameInStoreDto> gamesInShop;
}
