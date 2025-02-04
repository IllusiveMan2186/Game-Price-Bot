package com.gpb.telegram.command;

import com.gpb.telegram.filter.FilteredHandler;

import java.util.Locale;

/**
 * Class for handling commands that user writes to bot
 */
public interface CommandHandler extends FilteredHandler {

    /**
     * Returns a localized description of the change locale command.
     *
     * @param locale the {@link Locale} for which the description should be provided
     * @return a {@link String} containing the localized command description
     */
    String getDescription(Locale locale);
}
