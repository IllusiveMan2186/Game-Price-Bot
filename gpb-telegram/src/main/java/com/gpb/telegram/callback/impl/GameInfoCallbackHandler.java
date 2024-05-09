package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.mapper.GameInfoMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.service.TelegramUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component("gameInfo")
@AllArgsConstructor
public class GameInfoCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final TelegramUserService telegramUserService;
    private final GameInfoMapper gameInfoMapper;


    @Override
    @Transactional
    public TelegramResponse apply(String chatId, Update update, Locale locale) {
        long userId = update.getCallbackQuery().getFrom().getId();
        String messageText = update.getCallbackQuery().getData();
        long gameId = Long.parseLong(messageText.split(" ")[1]);

        Game game = gameService.getById(gameId);
        TelegramUser user = telegramUserService.getUserById(userId);

        return new TelegramResponse(gameInfoMapper.gameInfoToTelegramPage(game, user, chatId, locale));
    }
}
