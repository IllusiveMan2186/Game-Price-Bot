package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Consts;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component("getSynchronizeToken")
@FilterChainMarker(Consts.USER_EXISTING_FILTER)
public class GetWebConnectorTokenController implements TelegramController {

    private final MessageSource messageSource;
    private final TelegramUserService telegramUserService;

    public GetWebConnectorTokenController(MessageSource messageSource, TelegramUserService telegramUserService) {
        this.messageSource = messageSource;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("accounts.synchronization.get.token.description", null, locale);
    }

    @Override
    public SendMessage apply(String chatId, Update update, Locale locale) {
        long userId = update.getMessage().getFrom().getId();

        String token = telegramUserService.getWebUserConnectorToken(userId);

        return new SendMessage(chatId, token);
    }
}
