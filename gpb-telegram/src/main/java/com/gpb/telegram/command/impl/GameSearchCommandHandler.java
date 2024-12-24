package com.gpb.telegram.command.impl;

import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

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

        GameListPageDto page = gameService.getByName(gameName, pageNum);

        if (page.getGames().isEmpty()) {
            String errorMessage = String
                    .format(messageSource.getMessage("game.search.not.found.game", null, request.getLocale()), gameName);
            return new TelegramResponse(request.getChatId(), errorMessage);
        }

        return new TelegramResponse(gameListMapper.gameSearchListToTelegramPage(page.getGames(),
                request,
                page.getElementAmount(),
                pageNum,
                gameName));
    }
}
