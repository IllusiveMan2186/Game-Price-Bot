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

/**
 * Filter that ensures the Telegram user exists.
 * <p>
 * This filter checks if a user is already registered. If the user is not registered,
 * it creates a new TelegramUser based on the request details. If the user exists, it
 * retrieves and sets the user information in the request.
 * </p>
 */
@Slf4j
@Order(1)
@Component
@AllArgsConstructor
public class UserExistingFilter extends TelegramFilter {

    private final TelegramUserService telegramUserService;

    /**
     * Returns the unique key identifier for this filter.
     *
     * @return a {@link String} representing the key for the user existing filter.
     */
    @Override
    protected String getKey() {
        return Constants.USER_EXISTING_FILTER;
    }

    /**
     * Checks if the user exists. If the user is not registered, it creates a new user.
     * Otherwise, it sets the existing user in the Telegram request.
     *
     * @param request the {@link TelegramRequest} containing the user's Telegram details
     */
    @Override
    protected void checkFilter(final TelegramRequest request) {
        final long userId = request.getUserId();
        if (!telegramUserService.isUserRegistered(userId)) {
            log.info("User {} not found; registering new user.", userId);
            TelegramUser newUser = TelegramUser.builder()
                    .telegramId(userId)
                    .locale(request.getLocale())
                    .build();
            request.setUser(telegramUserService.createTelegramUser(newUser));
        } else {
            log.info("User {} exists; setting user information.", userId);
            request.setUser(telegramUserService.getUserById(userId));
        }
    }
}