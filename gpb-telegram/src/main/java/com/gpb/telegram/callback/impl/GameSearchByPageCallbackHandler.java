package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component("searchByPage")
@RequiredArgsConstructor
public class GameSearchByPageCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final MessageSource messageSource;
    private final GameListMapper gameListMapper;


    @Transactional
    @Override
    public TelegramResponse apply(String chatId, Update update, Locale locale) {
        String messageText = update.getCallbackQuery().getData();
        String gameName = update.getCallbackQuery().getData().replaceAll("/searchByPage \\d+ ", "");
        int pageNum = Integer.parseInt(messageText.split(" ")[1]);

        List<Game> games = gameService.getByName(gameName, pageNum);

        if (games.isEmpty()) {
            String errorMessage = String
                    .format(messageSource.getMessage("game.search.not.found.game", null, locale), gameName);
            return new TelegramResponse(Collections.singletonList(new SendMessage(chatId, errorMessage)));
        }

        long gameAmount = gameService.getGameAmountByName(gameName);

        return new TelegramResponse(gameListMapper.gameListToTelegramPage(games, gameAmount, chatId, pageNum, gameName, locale));
    }
}
