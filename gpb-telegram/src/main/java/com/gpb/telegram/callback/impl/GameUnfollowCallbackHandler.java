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
 * Callback handler for unfollowing a game.
 */
@Component("unsubscribe")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameUnfollowCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final MessageSource messageSource;

    /**
     * Processes the game unfollow callback.
     * <p>
     * This method extracts the game ID from the request arguments and marks the game as unfollowed
     * for the user by calling {@link GameService#setFollowGameOption(long, long, boolean)} with {@code false}.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing callback details and user context
     * @return a {@link TelegramResponse} with a confirmation message for the unfollow operation
     */
    @Override
    @Transactional
    public TelegramResponse apply(final TelegramRequest request) {
        final long gameId = request.getLongArgument(1);
        gameService.setFollowGameOption(gameId, request.getUserBasicId(), false);
        final String successMessage = messageSource.getMessage(
                "game.unsubscribe.success.message",
                null,
                request.getLocale());
        return new TelegramResponse(request.getChatId(), successMessage);
    }
}
