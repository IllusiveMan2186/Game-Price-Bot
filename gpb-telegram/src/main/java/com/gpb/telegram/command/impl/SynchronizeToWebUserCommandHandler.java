package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.service.TelegramUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.Locale;

@Component("synchronizeToWeb")
@AllArgsConstructor
public class SynchronizeToWebUserCommandHandler implements CommandHandler {

    private final MessageSource messageSource;
    private final TelegramUserService telegramUserService;

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("accounts.synchronization.description", null, locale);
    }

    @Override
    public TelegramResponse apply(String chatId, Update update, Locale locale) {
        long userId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText();
        String token = messageText.split(" ")[1];

        telegramUserService.synchronizeTelegramUser(token, userId);

        return new TelegramResponse(Collections.singletonList(new SendMessage(chatId,
                messageSource.getMessage("accounts.synchronization.token.connected.message", null, locale))));
    }
}
