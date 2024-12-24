package com.gpb.telegram.command.impl;

import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;

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
    public TelegramResponse apply(TelegramRequest request) {
        StringBuilder builder = new StringBuilder(messageSource.getMessage("help.menu.header.message", null, request.getLocale()));
        for (Map.Entry<String, CommandHandler> entrySet: controllers.entrySet()) {
            builder.append(System.lineSeparator())
                    .append("/").append(entrySet.getKey())
                    .append(entrySet.getValue().getDescription(request.getLocale()));
        }
        return new TelegramResponse(request, builder.toString());
    }
}
