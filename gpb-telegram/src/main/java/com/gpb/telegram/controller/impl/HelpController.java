package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.Map;

public class HelpController implements TelegramController {

    private final MessageSource messageSource;
    private final Map<String, TelegramController> controllers;

    public HelpController(Map<String, TelegramController> controllers, MessageSource messageSource){
        this.controllers = controllers;
        this.messageSource = messageSource;
    }

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("help.command.description", null, locale);
    }

    @Override
    public SendMessage apply(String chatId, Update update, Locale locale) {
        StringBuilder builder = new StringBuilder(messageSource.getMessage("help.menu.header.message", null, locale));
        for (Map.Entry<String, TelegramController> entrySet: controllers.entrySet()) {
            builder.append(System.lineSeparator())
                    .append("/").append(entrySet.getKey())
                    .append(entrySet.getValue().getDescription(locale));
        }
        return new SendMessage(chatId, builder.toString());
    }
}
