package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.service.TelegramUserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component("synchronizeToWeb")
public class SynchronizeToWebUserController implements TelegramController {

    private final MessageSource messageSource;
    private final TelegramUserService telegramUserService;

    public SynchronizeToWebUserController(MessageSource messageSource, TelegramUserService telegramUserService) {
        this.messageSource = messageSource;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("accounts.synchronization.description", null, locale);
    }

    @Override
    public SendMessage apply(String chatId, Update update, Locale locale) {
        long userId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText();
        String token = messageText.split(" ")[1];

        telegramUserService.synchronizeTelegramUser(token, userId);

        return new SendMessage(chatId,
                messageSource.getMessage("accounts.synchronization.token.connected.message", null, locale));
    }
}
