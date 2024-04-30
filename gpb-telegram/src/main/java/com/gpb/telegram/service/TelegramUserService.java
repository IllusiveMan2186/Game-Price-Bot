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
     * Create new telegram user
     *
     * @param newUser new user
     * @return new telegram user
     */
    TelegramUser createTelegramUser(TelegramUser newUser);

    /**
     * Connect web user to current telegram user
     *
     * @param token  token that connected to telegram user
     * @param telegramId telegram user id
     */
    void synchronizeTelegramUser(String token, long telegramId);

    /**
     * Get token for connect with telegram user
     *
     * @param telegramId telegram user id
     * @return token of connector
     */
    String getWebUserConnectorToken(long telegramId);

    Locale changeUserLocale(long telegramId,Locale newLocale);

    Locale getUserLocale(long telegramId);
}
