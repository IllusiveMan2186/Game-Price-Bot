package com.gpb.telegram.filter;

import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;

/**
 * Marker interface for classes that can be used as a Telegram filter handler.
 * <p>
 * Implementations of this interface process incoming {@link TelegramRequest} objects
 * and return a corresponding {@link TelegramResponse} as the output.
 * </p>
 */
public interface FilteredHandler {

    /**
     * Processes the provided Telegram request and returns a response.
     *
     * @param request the {@link TelegramRequest} containing the user's command and associated data
     * @return a {@link TelegramResponse} containing the result of processing the request
     */
    TelegramResponse apply(TelegramRequest request);
}
