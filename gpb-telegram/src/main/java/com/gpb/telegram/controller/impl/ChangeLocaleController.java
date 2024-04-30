package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import com.gpb.telegram.service.TelegramUserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component("changeLanguage")
public class ChangeLocaleController implements TelegramController {

    private final MessageSource messageSource;
    private final TelegramUserService telegramUserService;

    public ChangeLocaleController(MessageSource messageSource, TelegramUserService telegramUserService) {
        this.messageSource = messageSource;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public String getDescription(Locale locale) {
        return messageSource.getMessage("change.language.command.description", null, locale);
    }

    @Override
    public SendMessage apply(String chatId, Update update, Locale locale) {
        long userId = update.getMessage().getFrom().getId();
        String messageText = update.getMessage().getText();
        String language = messageText.split(" ")[1];

        locale = telegramUserService.changeUserLocale(userId, new Locale(language));
        return new SendMessage(chatId, messageSource.getMessage("change.language.command.successfully.message", null, locale));
    }
}
