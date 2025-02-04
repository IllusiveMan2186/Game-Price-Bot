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

/**
 * Command handler for searching games by name.
 */
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

    /**
     * Processes the search command.
     * <p>
     * This method extracts the game name from the message text , and returns a TelegramResponse with the mapped game list.
     * If no games are found, it returns a response with an error message.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the update with the search command
     * @return a {@link TelegramResponse} with the search results or an error message if no games are found
     */
    @Transactional
    @Override
    public TelegramResponse apply(TelegramRequest request) {
        final String gameName = request.getUpdate().getMessage().getText().replace("/search ", "").trim();
        final int pageNum = 1;

        final GameListPageDto gamePage = gameService.getByName(gameName, pageNum, request.getUserBasicId());

        if (gamePage.getGames().isEmpty()) {
            String errorMessage = String.format(
                    messageSource.getMessage("game.search.not.found.game", null, request.getLocale()),
                    gameName);
            return new TelegramResponse(request.getChatId(), errorMessage);
        }

        return new TelegramResponse(
                gameListMapper.mapGameSearchListToTelegramPage(
                        gamePage.getGames(),
                        request,
                        gamePage.getElementAmount(),
                        pageNum,
                        gameName
                )
        );
    }
}