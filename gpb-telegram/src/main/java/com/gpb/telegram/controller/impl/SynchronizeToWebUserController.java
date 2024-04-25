package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.service.TelegramUserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component("synchronizeToWeb")
public class SynchronizeToWebUserController implements TelegramController {

    private static final String SUCCESSFULLY_CONNECTED = "Successfully connected";

    private final TelegramUserService telegramUserService;

    public SynchronizeToWebUserController(TelegramUserService telegramUserService) {
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String getDescription() {
        return " {token} - synchronize telegram with web part by token";
    }

    @Override
    public SendMessage apply(String chatId, Update update) {
        long userId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText();
        String token = messageText.split(" ")[1];

        telegramUserService.synchronizeTelegramUser(token, userId);

        return new SendMessage(chatId, SUCCESSFULLY_CONNECTED);
    }
}
