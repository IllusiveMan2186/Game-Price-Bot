package com.gpb.telegram.filter;

import com.gpb.telegram.entity.TelegramRequest;

public class FilterChain {

    private final TelegramFilter firstFilter;

    public FilterChain(TelegramFilter firstFilter) {
        this.firstFilter = firstFilter;
    }

    public void handleFilterChain(FilteredHandler handler, TelegramRequest request) {
        firstFilter.handle(handler, request);
    }
}
