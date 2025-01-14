package com.gpb.telegram.command;

import com.gpb.telegram.filter.FilteredHandler;

import java.util.Locale;

/**
 * Class for handling commands that user writes to bot
 */
public interface CommandHandler extends FilteredHandler {

    /**
     * Gives a description about command
     *
     * @return description
     */
    String getDescription(Locale locale);
}
