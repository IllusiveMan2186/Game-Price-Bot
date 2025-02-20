package com.gpb.telegram.callback.impl;

import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Callback handler for subscribing to game follow notifications.
 */
@Component("subscribe")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameFollowCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final MessageSource messageSource;

    /**
     * Processes the game follow callback.
     * <p>
     * This method extracts the game ID from the request arguments and uses the {@link GameService} to mark the game
     * as followed for the user. A success message is then returned in a {@link TelegramResponse}.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the callback details and user context
     * @return a {@link TelegramResponse} with a localized success message
     */
    @Override
    @Transactional
    public TelegramResponse apply(final TelegramRequest request) {
        final long gameId = request.getLongArgument(1);
        gameService.setFollowGameOption(gameId, request.getUserBasicId(), true);

        final String successMessage = messageSource.getMessage(
                "game.subscribe.success.message",
                null,
                request.getLocale());

        return new TelegramResponse(request.getChatId(), successMessage);
    }
}