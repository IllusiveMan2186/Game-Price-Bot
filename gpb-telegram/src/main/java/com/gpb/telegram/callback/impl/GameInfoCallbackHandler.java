package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.mapper.GameInfoMapper;
import com.gpb.telegram.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component("gameInfo")
@AllArgsConstructor
public class GameInfoCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final GameInfoMapper gameInfoMapper;


    @Override
    public TelegramResponse apply(String chatId, Update update, Locale locale) {
        String messageText = update.getCallbackQuery().getData();
        long gameId = Long.parseLong(messageText.split(" ")[1]);

        Game game = gameService.getById(gameId);

        return new TelegramResponse(gameInfoMapper.gameInfoToTelegramPage(game, chatId, locale));
    }
}
