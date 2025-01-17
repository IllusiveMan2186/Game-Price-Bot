package com.gpb.telegram.service.impl;

import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.CommonRequestHandlerService;
import com.gpb.telegram.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonRequestHandlerServiceImpl implements CommonRequestHandlerService {

    private final GameService gameService;
    private final MessageSource messageSource;
    private final GameListMapper gameListMapper;

    public TelegramResponse processUserGameListRequest(TelegramRequest request, int pageNum) {
        GameListPageDto page = gameService.getUserGames(request.getUserBasicId(), pageNum);

        if (page.getGames().isEmpty()) {
            String errorMessage = messageSource.getMessage("user.game.list.empty", null, request.getLocale());
            return new TelegramResponse(request.getChatId(), errorMessage);
        }

        return new TelegramResponse(
                gameListMapper.mapUserGameListToTelegramPage(
                        page.getGames(),
                        request,
                        page.getElementAmount(),
                        pageNum));
    }

    @Override
    public TelegramResponse processGameListRequest(TelegramRequest request, int pageNum, String sort) {
        GameListPageDto page = gameService.getGameList(pageNum, sort);

        if (page.getGames().isEmpty()) {
            String errorMessage = messageSource.getMessage(
                    "game.list.not.found.game",
                    null,
                    request.getLocale());
            return new TelegramResponse(request.getChatId(), errorMessage);
        }

        return new TelegramResponse(
                gameListMapper.mapGameListToTelegramPage(
                        page.getGames(),
                        request,
                        page.getElementAmount(),
                        pageNum,
                        sort));
    }
}
