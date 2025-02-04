package com.gpb.telegram.command.impl;

import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Command handler for changing the locale (language) of a Telegram user.
 */
@Component("lang")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class ChangeLocaleCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final TelegramUserService telegramUserService;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("change.language.command.description", null, locale);
    }

    /**
     * Applies the change locale command.
     * <p>
     * This method retrieves the language argument from the {@link TelegramRequest}, updates the user's locale
     * using the {@link TelegramUserService}, and returns a {@link TelegramResponse} with a success message.
     * </p>
     *
     * @param request the {@link TelegramRequest} containing the command details and arguments
     * @return a {@link TelegramResponse} indicating that the locale was successfully updated
     */
    @Override
    public TelegramResponse apply(TelegramRequest request) {
        final String language = request.getArgument(1);
        final Locale newLocale = new Locale(language);
        request.setLocale(telegramUserService.changeUserLocale(request.getUserId(), newLocale));
        final String successMessage = messageSource.getMessage("change.language.command.successfully.message", null, request.getLocale());
        return new TelegramResponse(request, successMessage);
    }
}
