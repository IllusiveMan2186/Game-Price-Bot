package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.filter.FilterChainMarker;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
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
    public TelegramResponse apply(String chatId, Update update, Locale locale) {
        long userId = update.getMessage().getFrom().getId();

        String token = telegramUserService.getWebUserConnectorToken(userId);

        return new TelegramResponse(Collections.singletonList(new SendMessage(chatId, token)));
    }
}
