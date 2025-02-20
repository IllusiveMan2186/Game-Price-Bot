package com.gpb.common.entity.game;

import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private List<GameInStoreDto> gamesInShop;
}
