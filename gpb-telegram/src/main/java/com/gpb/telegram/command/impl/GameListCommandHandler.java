package com.gpb.telegram.command.impl;

import com.gpb.common.util.CommonConstants;
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
 * Command handler for retrieving a paginated list of games.
 */
@Component("gameList")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameListCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final CommonRequestHandlerService commonRequestHandlerService;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("game.list.description", null, locale);
    }

    /**
     * Processes the game list command.
     * <p>
     * This method extracts sorting parameters from the request arguments (if provided) and defaults to page 1.
     * It delegates the processing of the game list request to the {@link CommonRequestHandlerService}.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing command arguments
     * @return a {@link TelegramResponse} with the list of games and associated metadata
     */
    @Override
    public TelegramResponse apply(TelegramRequest request) {
        final int pageNum = 1;
        final String sort = getSortParam(request);
        return commonRequestHandlerService.processGameListRequest(request, pageNum, sort);
    }

    /**
     * Extracts the sort parameter from the Telegram request.
     * <p>
     * This method checks for specific sort parameters from the request arguments:
     * if the first argument equals the UI format for a price sort, then the price sort parameter is used;
     * otherwise, the name sort parameter is used. Similarly, it determines the sort direction.
     * If no arguments are provided, default sort parameters are applied.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing command arguments
     * @return a {@link String} combining the sort parameter and sort direction (e.g., "price-asc")
     */
    private String getSortParam(final TelegramRequest request) {
        String sortParam;
        try {
            sortParam = Constants.PRICE_SORT_PARAM_TELEGRAM_UI_FORMAT.equals(request.getArgument(1))
                    ? CommonConstants.PRICE_SORT_PARAM
                    : CommonConstants.NAME_SORT_PARAM;
        } catch (ArrayIndexOutOfBoundsException e) {
            sortParam = CommonConstants.NAME_SORT_PARAM;
        }

        String sortDirection;
        try {
            sortDirection = CommonConstants.SORT_DIRECTION_DESCENDING.toLowerCase().equals(request.getArgument(2))
                    ? CommonConstants.SORT_DIRECTION_DESCENDING
                    : CommonConstants.SORT_DIRECTION_ASCENDING;
        } catch (ArrayIndexOutOfBoundsException e) {
            sortDirection = CommonConstants.SORT_DIRECTION_ASCENDING;
        }

        return sortParam + "-" + sortDirection;
    }
}
