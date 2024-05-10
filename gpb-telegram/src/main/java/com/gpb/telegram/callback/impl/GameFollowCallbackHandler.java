package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.service.GameStoresService;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("subscribe")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameFollowCallbackHandler implements CallbackHandler {

    private final TelegramUserService userService;
    private final GameService gameService;
    private final GameStoresService storesService;
    private MessageSource messageSource;

    @Override
    @Transactional
    public TelegramResponse apply(TelegramRequest request) {
        long gameId = request.getLongArgument(1);
        userService.subscribeToGame(request.getUserId(), gameId);
        Game game = gameService.getById(gameId);
        if (!game.isFollowed()) {
            storesService.subscribeToGame(gameId);
        }
        return new TelegramResponse(request.getChatId(), messageSource.getMessage("game.subscribe.success.message",
                null, request.getLocale()) + game.getName());
    }
}
