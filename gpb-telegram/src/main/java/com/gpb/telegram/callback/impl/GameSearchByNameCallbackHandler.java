package com.gpb.telegram.callback.impl;

import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Callback handler for searching games by name.
 */
@Component("searchByName")
@RequiredArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameSearchByNameCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final MessageSource messageSource;
    private final GameListMapper gameListMapper;

    /**
     * Processes the callback request for game search by name.
     * <p>
     * This method extracts the game name from the callback query data .
     * It then retrieves the page number from the request , and finally maps the result to a TelegramResponse.
     * If no games are found, a localized error message is returned.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the callback data and arguments
     * @return a {@link TelegramResponse} containing either the mapped game list or an error message if no games are found
     */
    @Override
    @Transactional
    public TelegramResponse apply(final TelegramRequest request) {
        // Extract game name from callback data by removing the command and numeric prefix.
        final String gameName = request.getUpdate().getCallbackQuery().getData()
                .replaceAll("/searchByName \\d+ ", "").trim();
        final int pageNum = request.getIntArgument(1);

        final GameListPageDto page = gameService.getByName(gameName, pageNum, request.getUserBasicId());

        if (page.getElementAmount() < 1) {
            final String errorMessage = String.format(
                    messageSource.getMessage("game.search.not.found.game", null, request.getLocale()),
                    gameName);
            return new TelegramResponse(request, errorMessage);
        }

        return new TelegramResponse(
                gameListMapper.mapGameSearchListToTelegramPage(
                        page.getGames(),
                        request,
                        page.getElementAmount(),
                        pageNum,
                        gameName));
    }
}
