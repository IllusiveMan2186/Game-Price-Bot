package com.gpb.telegram.callback.impl;

import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

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

        GameListPageDto page = gameService.getByName(gameName, pageNum);

        if (page.getElementAmount() < 1) {
            String errorMessage = String
                    .format(messageSource.getMessage("game.search.not.found.game", null, request.getLocale()), gameName);
            return new TelegramResponse(request, errorMessage);
        }

        return new TelegramResponse(gameListMapper.gameSearchListToTelegramPage(page.getGames(), request, page.getElementAmount(), pageNum, gameName));
    }
}
