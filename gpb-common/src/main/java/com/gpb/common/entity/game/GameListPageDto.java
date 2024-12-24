package com.gpb.common.entity.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameListPageDto {

    private long elementAmount;

    private List<GameDto> games;
}
