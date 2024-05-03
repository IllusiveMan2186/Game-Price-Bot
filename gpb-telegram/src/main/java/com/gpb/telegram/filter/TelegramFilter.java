package com.gpb.telegram.filter;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public abstract class TelegramFilter {

    private TelegramFilter nextFilter = null;

    public void setNextFilter(TelegramFilter filter) {
        nextFilter = filter;
    }


    public void handle(FilteredHandler handler, Update update) {
        FilterChainMarker marker = handler
                .getClass()
                .getAnnotation(FilterChainMarker.class);

        if (marker != null) {
            List<String> filters = List.of(marker.value());

            if (filters.contains(getKey())) {
                checkFilter(update);
            }
        }

        if (nextFilter != null) {
            nextFilter.handle(handler, update);
        }
    }

    protected abstract String getKey();

    protected abstract void checkFilter(Update update);
}
