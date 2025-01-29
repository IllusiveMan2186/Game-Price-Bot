package com.gpb.telegram.command.impl;

import com.gpb.common.exception.RestTemplateRequestException;
import com.gpb.common.service.UserLinkerService;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component(Constants.SYNCHRONIZATION_COMMAND)
@AllArgsConstructor
@FilterChainMarker(Constants.USER_EXISTING_FILTER)
public class LinkedToWebUserCommandHandler implements CommandHandler {

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

        try {
            userLinkerService.linkAccounts(token, request.getUserBasicId());

            return new TelegramResponse(request,
                    messageSource.getMessage("accounts.synchronization.token.connected.message", null,
                            request.getLocale()));
        } catch (RestTemplateRequestException exception) {
            return new TelegramResponse(request,
                    messageSource.getMessage("accounts.linked.already", null,
                            request.getLocale()));
        }
    }
}
