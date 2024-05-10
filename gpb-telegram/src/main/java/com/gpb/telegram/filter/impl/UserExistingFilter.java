package com.gpb.telegram.filter.impl;


import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.filter.TelegramFilter;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    protected void checkFilter(TelegramRequest request) {
        long userId = request.getUserId();
        if (!telegramUserService.isUserRegistered(userId)) {
            TelegramUser newUser = TelegramUser.builder()
                    .telegramId(userId)
                    .locale(request.getLocale())
                    .build();
            request.setUser(telegramUserService.createTelegramUser(newUser));
        }
        request.setUser(telegramUserService.getUserById(request.getUserId()));
    }
}
