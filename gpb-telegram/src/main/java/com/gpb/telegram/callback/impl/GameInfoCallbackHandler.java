package com.gpb.telegram.callback.impl;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.mapper.GameInfoMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("gameInfo")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameInfoCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final GameInfoMapper gameInfoMapper;


    @Override
    @Transactional
    public TelegramResponse apply(TelegramRequest request) {

        GameInfoDto game = gameService.getById(request.getLongArgument(1), request.getUserBasicId());

        return new TelegramResponse(gameInfoMapper.mapGameInfoToTelegramPage(game, request));
    }
}
