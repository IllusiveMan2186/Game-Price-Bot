package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component("search")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
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
    public TelegramResponse apply(TelegramRequest request) {
        String gameName = request.getUpdate().getMessage().getText().replace("/search ", "");
        int pageNum = 1;

        List<Game> games = gameService.getByName(gameName, pageNum);

        if (games.isEmpty()) {
            String errorMessage = String
                    .format(messageSource.getMessage("game.search.not.found.game", null, request.getLocale()), gameName);
            return new TelegramResponse(request.getChatId(), errorMessage);
        }

        long gameAmount = gameService.getGameAmountByName(gameName);

        return new TelegramResponse(gameListMapper.gameSearchListToTelegramPage(games, request, gameAmount, pageNum,
                gameName));
    }
}
