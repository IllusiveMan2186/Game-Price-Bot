package com.gpb.common.entity.game;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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

    @Min(0)
    private long elementAmount;

    @NotEmpty
    private List<GameDto> games;
}
