package com.gpb.telegram.filter.impl;


import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.filter.TelegramFilter;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
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
            log.info("Register new user {}", userId);
            TelegramUser newUser = TelegramUser.builder()
                    .telegramId(userId)
                    .locale(request.getLocale())
                    .build();
            request.setUser(telegramUserService.createTelegramUser(newUser));
        } else {
            log.info("Set user {}", userId);
            request.setUser(telegramUserService.getUserById(request.getUserId()));
        }
    }
}
