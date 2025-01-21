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

    @Override
    public TelegramResponse apply(TelegramRequest request) {
        String language = request.getArgument(1);

        request.setLocale(telegramUserService.changeUserLocale(request.getUserId(), new Locale(language)));
        return new TelegramResponse(request, messageSource
                        .getMessage("change.language.command.successfully.message", null, request.getLocale()));
    }
}
