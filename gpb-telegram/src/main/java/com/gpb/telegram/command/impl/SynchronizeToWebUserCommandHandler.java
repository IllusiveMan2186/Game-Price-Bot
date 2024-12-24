package com.gpb.telegram.command.impl;

import com.gpb.common.service.UserLinkerService;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
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
    private final TelegramUserService telegramUserService;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("accounts.synchronization.description", null, locale);
    }

    @Override
    @Transactional
    public TelegramResponse apply(TelegramRequest request) {
        String token = request.getArgument(1);

        Long newUserBasicId = userLinkerService.linkAccounts(token, request.getUserBasicId());
        telegramUserService.setBasicUserId(request.getUserBasicId(), newUserBasicId);

        return new TelegramResponse(request,
                messageSource.getMessage("accounts.synchronization.token.connected.message", null,
                        request.getLocale()));
    }
}
