package com.gpb.telegram.service;

import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;

/**
 * Service for handling requests in Telegram bots.
 * <p>
 * This service centralizes logic for processing requests,
 * allowing reuse across both command and callback handlers.
 * </p>
 */
public interface CommonRequestHandlerService {

    /**
     * Process a request to get a list of games the user is subscribed to
     *
     * @param request telegram request
     * @param pageNum number of page list
     * @return response with list of games
     */
    TelegramResponse processUserGameListRequest(TelegramRequest request, int pageNum);

    /**
     * Process a request to get a list of games with sort param
     *
     * @param request telegram request
     * @param pageNum number of page list
     * @param sort    sort param
     * @return response with list of games
     */
    TelegramResponse processGameListRequest(TelegramRequest request, int pageNum, String sort);
}
