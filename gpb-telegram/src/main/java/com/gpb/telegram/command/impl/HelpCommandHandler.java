package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.command.CommandHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

@AllArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final Map<String, CommandHandler> controllers;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("help.command.description", null, locale);
    }

    @Override
    public TelegramResponse apply(String chatId, Update update, Locale locale) {
        StringBuilder builder = new StringBuilder(messageSource.getMessage("help.menu.header.message", null, locale));
        for (Map.Entry<String, CommandHandler> entrySet: controllers.entrySet()) {
            builder.append(System.lineSeparator())
                    .append("/").append(entrySet.getKey())
                    .append(entrySet.getValue().getDescription(locale));
        }
        return new TelegramResponse(Collections.singletonList(new SendMessage(chatId, builder.toString())));
    }
}
