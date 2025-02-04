package com.gpb.telegram.callback.impl;

import com.gpb.telegram.callback.CallbackHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.CommonRequestHandlerService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Callback handler for processing user game list requests.
 */
@Component("userGameListCallback")
@RequiredArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class UserGameListCallbackHandler implements CallbackHandler {

    private final CommonRequestHandlerService commonRequestHandlerService;

    /**
     * Processes the user game list callback request.
     * <p>
     * This method retrieves the page number from the request arguments and uses the
     * {@link CommonRequestHandlerService} to process and obtain the user's game list.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing callback details and command arguments
     * @return a {@link TelegramResponse} with the processed user game list information
     */
    @Override
    @Transactional
    public TelegramResponse apply(final TelegramRequest request) {
        final int pageNum = request.getIntArgument(1);
        return commonRequestHandlerService.processUserGameListRequest(request, pageNum);
    }
}
