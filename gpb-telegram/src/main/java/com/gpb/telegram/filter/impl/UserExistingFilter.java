package com.gpb.telegram.filter.impl;


import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.filter.TelegramFilter;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Consts;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Order(1)
@Component
public class UserExistingFilter extends TelegramFilter {

    private final TelegramUserService telegramUserService;

    protected UserExistingFilter(TelegramUserService telegramUserService) {
        this.telegramUserService = telegramUserService;
    }

    @Override
    protected String getKey() {
        return Consts.USER_EXISTING_FILTER;
    }

    @Override
    protected void checkFilter(Update update) {
        long userId = update.getMessage().getFrom().getId();
        if (!telegramUserService.isUserRegistered(userId)) {
            TelegramUser newUser = TelegramUser.builder()
                    .telegramId(userId)
                    .build();
            telegramUserService.createTelegramUser(newUser);
        }

    }
}
