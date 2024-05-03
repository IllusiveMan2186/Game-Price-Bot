package com.gpb.telegram.filter;

import com.gpb.telegram.bean.TelegramResponse;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

/**
 * Interface marks class that it could be used with telegram filter
 */
public interface FilteredHandler {

    /**
     * Apply command from user
     *
     * @param chatId chat id
     * @param update information about request
     * @return message
     */
    TelegramResponse apply(String chatId, Update update, Locale locale);
}
