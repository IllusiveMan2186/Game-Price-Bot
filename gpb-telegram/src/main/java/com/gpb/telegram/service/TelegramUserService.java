package com.gpb.telegram.service;

import com.gpb.telegram.bean.TelegramUser;

import java.util.Locale;

public interface TelegramUserService {

    /**
     * Check if user with such telegram user id exist
     *
     * @param telegramId telegram user id
     * @return user
     */
    boolean isUserRegistered(long telegramId);

    /**
     * Get user by telegram id
     *
     * @param telegramId telegram id
     * @return user
     */
    TelegramUser getUserById(long telegramId);

    /**
     * Create new telegram user
     *
     * @param newUser new user
     * @return new telegram user
     */
    TelegramUser createTelegramUser(TelegramUser newUser);

    /**
     * Change user locale
     *
     * @param telegramId user id in telegram
     * @param newLocale new locale
     * @return updated locale
     */
    Locale changeUserLocale(long telegramId,Locale newLocale);

    /**
     * Get users locale
     *
     * @param telegramId user id in telegram
     * @return users locale
     */
    Locale getUserLocale(long telegramId);
}
