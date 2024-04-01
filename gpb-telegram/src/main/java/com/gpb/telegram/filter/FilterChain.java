package com.gpb.telegram.filter;

import com.gpb.telegram.controller.TelegramController;
import org.telegram.telegrambots.meta.api.objects.Update;

public class FilterChain {

    private final TelegramFilter firstFilter;

    public FilterChain(TelegramFilter firstFilter) {
        this.firstFilter = firstFilter;
    }

    public void handleFilterChain(TelegramController controller, Update update) {
        firstFilter.handle(controller, update);
    }
}
