package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("searchByPage")
@RequiredArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameSearchByPageCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final MessageSource messageSource;
    private final GameListMapper gameListMapper;


    @Transactional
    @Override
    public TelegramResponse apply(TelegramRequest request) {
        String gameName = request.getUpdate().getCallbackQuery().getData().replaceAll("/searchByPage \\d+ ", "");
        int pageNum = request.getIntArgument(1);

        List<Game> games = gameService.getByName(gameName, pageNum);

        if (games.isEmpty()) {
            String errorMessage = String
                    .format(messageSource.getMessage("game.search.not.found.game", null, request.getLocale()), gameName);
            return new TelegramResponse(request, errorMessage);
        }

        long gameAmount = gameService.getGameAmountByName(gameName);

        return new TelegramResponse(gameListMapper.gameSearchListToTelegramPage(games, request, gameAmount, pageNum,
                gameName));
    }
}
