package com.gpb.telegram.filter.impl;


import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.filter.TelegramFilter;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Order(1)
@Component
@AllArgsConstructor
public class UserExistingFilter extends TelegramFilter {

    private final TelegramUserService telegramUserService;

    @Override
    protected String getKey() {
        return Constants.USER_EXISTING_FILTER;
    }

    @Override
    protected void checkFilter(Update update) {
        long userId = update.hasCallbackQuery()
                ? update.getCallbackQuery().getFrom().getId()
                : update.getMessage().getFrom().getId();
        if (!telegramUserService.isUserRegistered(userId)) {
            Locale locale = new Locale(update.getMessage().getFrom().getLanguageCode());
            TelegramUser newUser = TelegramUser.builder()
                    .telegramId(userId)
                    .locale(locale)
                    .build();
            telegramUserService.createTelegramUser(newUser);
        }

    }
}
