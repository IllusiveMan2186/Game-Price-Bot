package com.gpb.telegram.filter;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;

/**
 * Interface marks class that it could be used with telegram filter
 */
public interface FilteredHandler {

    /**
     * Apply command from user
     *
     * @param request telegram request
     * @return message
     */
    TelegramResponse apply(TelegramRequest request);
}
