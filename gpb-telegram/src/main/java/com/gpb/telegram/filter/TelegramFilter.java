package com.gpb.telegram.filter;

import com.gpb.telegram.entity.TelegramRequest;
import org.springframework.aop.framework.AopProxyUtils;

import java.util.List;

public abstract class TelegramFilter {

    private TelegramFilter nextFilter = null;

    public void setNextFilter(TelegramFilter filter) {
        nextFilter = filter;
    }


    public void handle(FilteredHandler handler, TelegramRequest request) {
        FilterChainMarker marker = AopProxyUtils.ultimateTargetClass(handler)
                .getAnnotation(FilterChainMarker.class);

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

    protected abstract String getKey();

    protected abstract void checkFilter(TelegramRequest request);
}
