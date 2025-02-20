package com.gpb.telegram.command.impl;

import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/**
 * Command handler for generating the help menu.
 */
@AllArgsConstructor
@Component("help")
public class HelpCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final Map<String, CommandHandler> controllers;

    @Override
    public String getDescription(final Locale locale) {
        return messageSource.getMessage("help.command.description", null, locale);
    }

    /**
     * Processes the help command by generating a menu of available commands with their descriptions.
     *
     * @param request the {@link TelegramRequest} containing command details and locale information
     * @return a {@link TelegramResponse} containing the dynamically generated help menu
     */
    @Override
    public TelegramResponse apply(final TelegramRequest request) {
        final Locale locale = request.getLocale();
        final String headerMessage = messageSource.getMessage("help.menu.header.message", null, locale);
        final StringBuilder builder = new StringBuilder(headerMessage);

        controllers.forEach((command, handler) ->
                builder.append(System.lineSeparator())
                        .append("/")
                        .append(command)
                        .append(" ")
                        .append(handler.getDescription(locale))
        );

        return new TelegramResponse(request, builder.toString());
    }
}
