package com.gpb.telegram.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameListPageDto {

    private long elementAmount;

    private List<GameDto> games;
}
