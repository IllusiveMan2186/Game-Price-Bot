package com.gpb.telegram.callback.impl;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.mapper.GameInfoMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Callback handler for retrieving detailed information about a game.
 */
@Component("gameInfo")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameInfoCallbackHandler implements CallbackHandler {

    private final GameService gameService;
    private final GameInfoMapper gameInfoMapper;

    /**
     * Processes the game info callback request.
     * <p>
     * This method extracts the game ID from the request arguments and retrieves the corresponding game details
     * using the {@link GameService}. It then maps the game information to a Telegram page representation and
     * returns a {@link TelegramResponse} containing the result.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the callback details and user context
     * @return a {@link TelegramResponse} with the mapped game information
     */
    @Override
    @Transactional
    public TelegramResponse apply(final TelegramRequest request) {
        final long gameId = request.getLongArgument(1);
        final long userBasicId = request.getUserBasicId();
        final GameInfoDto game = gameService.getById(gameId, userBasicId);

        return new TelegramResponse(gameInfoMapper.mapGameInfoToTelegramPage(game, request));
    }
}