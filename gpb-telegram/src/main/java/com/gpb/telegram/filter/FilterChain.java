package com.gpb.telegram.filter;

import org.telegram.telegrambots.meta.api.objects.Update;

public class FilterChain {

    private final TelegramFilter firstFilter;

    public FilterChain(TelegramFilter firstFilter) {
        this.firstFilter = firstFilter;
    }

    public void handleFilterChain(FilteredHandler handler, Update update) {
        firstFilter.handle(handler, update);
    }
}
