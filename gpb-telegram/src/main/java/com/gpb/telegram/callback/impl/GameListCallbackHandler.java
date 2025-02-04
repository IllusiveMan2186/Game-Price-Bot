package com.gpb.telegram.callback.impl;

import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.CommonRequestHandlerService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Callback handler for processing game list requests.
 */
@Component("gameListCallback")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GameListCallbackHandler implements CallbackHandler {

    private final CommonRequestHandlerService commonRequestHandlerService;

    /**
     * Processes the game list callback request.
     * <p>
     * This method extracts the page number and sort parameters from the request arguments and invokes
     * the {@link CommonRequestHandlerService#processGameListRequest(TelegramRequest, int, String)} method to process
     * the game list request.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the callback details and command arguments
     * @return a {@link TelegramResponse} containing the processed game list information
     */
    @Override
    public TelegramResponse apply(final TelegramRequest request) {
        final int pageNum = request.getIntArgument(1);
        final String sort = request.getArgument(2);
        return commonRequestHandlerService.processGameListRequest(request, pageNum, sort);
    }
}
