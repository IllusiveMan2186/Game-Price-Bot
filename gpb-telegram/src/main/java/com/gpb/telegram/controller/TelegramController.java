package com.gpb.telegram.controller;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

public interface TelegramController {

    /**
     * Gives a description about command
     *
     * @return description
     */
    String getDescription(Locale locale);

    /**
     * Apply command from user
     *
     * @param chatId chat id
     * @param update information about request
     * @return message
     */
    SendMessage apply(String chatId, Update update, Locale locale);
}
