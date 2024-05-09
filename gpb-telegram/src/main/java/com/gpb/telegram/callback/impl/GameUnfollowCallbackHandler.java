package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
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
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component("unsubscribe")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameUnfollowCallbackHandler implements CallbackHandler {

    private final TelegramUserService userService;
    private final GameService gameService;
    private final GameStoresService storesService;
    private MessageSource messageSource;

    @Override
    @Transactional
    public TelegramResponse apply(String chatId, Update update, Locale locale) {
        long userId = update.getCallbackQuery().getFrom().getId();
        String messageText = update.getCallbackQuery().getData();
        long gameId = Long.parseLong(messageText.split(" ")[1]);
        userService.unsubscribeFromGame(userId, gameId);
        Game game = gameService.getById(gameId);

        if (game.isFollowed() && game.getUserList().isEmpty()) {
            storesService.unsubscribeFromGame(gameId);
        }
        return new TelegramResponse(chatId, messageSource.getMessage("game.unsubscribe.success.message",
                null, locale) + game.getName());
    }
}
