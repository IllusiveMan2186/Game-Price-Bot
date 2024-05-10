package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.callback.CallbackHandler;
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

        Game game = gameService.getById(request.getLongArgument(1));

        return new TelegramResponse(gameInfoMapper.gameInfoToTelegramPage(game, request));
    }
}
