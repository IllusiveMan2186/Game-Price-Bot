package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.UserLinkerService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component("synchronizeToWeb")
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class SynchronizeToWebUserCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final UserLinkerService userLinkerService;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("accounts.synchronization.description", null, locale);
    }

    @Override
    public TelegramResponse apply(TelegramRequest request) {
        String token = request.getArgument(1);

        userLinkerService.linkAccounts(token, request.getUserBasicId());

        return new TelegramResponse(request,
                messageSource.getMessage("accounts.synchronization.token.connected.message", null,
                        request.getLocale()));
    }
}
