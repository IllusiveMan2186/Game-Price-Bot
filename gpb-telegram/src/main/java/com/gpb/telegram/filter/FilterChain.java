package com.gpb.telegram.filter;

import com.gpb.telegram.entity.TelegramRequest;

/**
 * Represents a chain of Telegram filters that processes incoming requests.
 * <p>
 * This class encapsulates the first filter in the chain and provides a method to start processing
 * a {@link TelegramRequest} using a specified {@link FilteredHandler}.
 * </p>
 */
public class FilterChain {

    private final TelegramFilter firstFilter;

    public FilterChain(final TelegramFilter firstFilter) {
        this.firstFilter = firstFilter;
    }

    /**
     * Initiates processing of the Telegram request through the filter chain.
     *
     * @param handler the {@link FilteredHandler} that will process the request after filtering
     * @param request the {@link TelegramRequest} to be processed through the chain
     */
    public void handleFilterChain(final FilteredHandler handler, final TelegramRequest request) {
        firstFilter.handle(handler, request);
    }
}