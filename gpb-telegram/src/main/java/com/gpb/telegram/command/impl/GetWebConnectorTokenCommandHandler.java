package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("getSynchronizeToken")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class GetWebConnectorTokenCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final TelegramUserService telegramUserService;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("accounts.synchronization.get.token.description", null, locale);
    }

    @Override
    public TelegramResponse apply(TelegramRequest request) {
        String token = telegramUserService.getWebUserConnectorToken(request.getUserId());

        return new TelegramResponse(request, token);
    }
}
