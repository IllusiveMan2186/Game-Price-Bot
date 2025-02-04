package com.gpb.telegram.command.impl;

import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.CommonRequestHandlerService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Command handler for retrieving the list of games associated with the user.
 */
@Component("userGameList")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class UserGameListCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final CommonRequestHandlerService commonRequestHandlerService;

    @Override
    public String getDescription(final Locale locale) {
        return messageSource.getMessage("game.user.game.list.command.description", null, locale);
    }

    /**
     * Processes the command to retrieve the user's game list.
     * <p>
     * This method delegates the request processing to the {@link CommonRequestHandlerService}, passing
     * the TelegramRequest and a default page number (1). It returns a {@link TelegramResponse} with the resulting
     * game list data.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the command details and user context
     * @return a {@link TelegramResponse} with the processed game list information
     */
    @Override
    public TelegramResponse apply(final TelegramRequest request) {
        final int pageNumber = 1;
        return commonRequestHandlerService.processUserGameListRequest(request, pageNumber);
    }
}