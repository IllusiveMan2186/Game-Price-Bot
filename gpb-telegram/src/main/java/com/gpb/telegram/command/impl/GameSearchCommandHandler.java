package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component("search")
@AllArgsConstructor
public class GameSearchCommandHandler implements CommandHandler {

    private final GameService gameService;
    private final MessageSource messageSource;
    private final GameListMapper gameListMapper;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("game.search.command.description", null, locale);
    }

    @Transactional
    @Override
    public TelegramResponse apply(String chatId, Update update, Locale locale) {
        String gameName = update.getMessage().getText().replace("/search ", "");
        int pageNum = 1;

        List<Game> games = gameService.getByName(gameName, pageNum);

        if (games.isEmpty()) {
            String errorMessage = String
                    .format(messageSource.getMessage("game.search.not.found.game", null, locale), gameName);
            return new TelegramResponse(Collections.singletonList(new SendMessage(chatId, errorMessage)));
        }

        long gameAmount = gameService.getGameAmountByName(gameName);

        return new TelegramResponse(gameListMapper.gameSearchListToTelegramPage(games, gameAmount, chatId, pageNum, gameName, locale));
    }
}
