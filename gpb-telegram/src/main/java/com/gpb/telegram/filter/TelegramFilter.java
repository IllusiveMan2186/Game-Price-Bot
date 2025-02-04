package com.gpb.telegram.filter;

import com.gpb.telegram.entity.TelegramRequest;
import org.springframework.aop.framework.AopProxyUtils;

import java.util.List;

/**
 * Abstract base class for Telegram filters that implement a chain-of-responsibility pattern.
 * <p>
 * Each filter in the chain examines an incoming {@link TelegramRequest} and applies its specific
 * check if applicable. After processing, the request is passed along to the next filter in the chain.
 * </p>
 */
public abstract class TelegramFilter {

    private TelegramFilter nextFilter;

    /**
     * Sets the next filter in the chain.
     *
     * @param filter the next {@link TelegramFilter} to be executed after the current one
     */
    public void setNextFilter(final TelegramFilter filter) {
        this.nextFilter = filter;
    }

    /**
     * Processes the incoming Telegram request by determining if the current filter should be applied,
     * and if so, executing its specific check. Afterwards, the request is forwarded to the next filter
     * in the chain, if any.
     *
     * @param handler the {@link FilteredHandler} that processes the request after filtering
     * @param request the {@link TelegramRequest} to be processed
     */
    public void handle(final FilteredHandler handler, final TelegramRequest request) {
        FilterChainMarker marker = AopProxyUtils.ultimateTargetClass(handler).getAnnotation(FilterChainMarker.class);

        if (marker != null) {
            List<String> filters = List.of(marker.value());

            if (filters.contains(getKey())) {
                checkFilter(request);
            }
        }

        if (nextFilter != null) {
            nextFilter.handle(handler, request);
        }
    }

    /**
     * Returns the unique key identifier for this filter.
     *
     * @return a {@link String} representing the filter's unique key
     */
    protected abstract String getKey();

    /**
     * Performs the specific check or processing logic for this filter on the given Telegram request.
     *
     * @param request the {@link TelegramRequest} to be processed by this filter
     */
    protected abstract void checkFilter(TelegramRequest request);
}
