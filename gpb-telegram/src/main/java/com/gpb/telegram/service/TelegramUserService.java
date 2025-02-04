package com.gpb.telegram.service;

import com.gpb.telegram.entity.TelegramUser;

import java.util.Locale;

/**
 * Service interface for managing Telegram user operations.
 * <p>
 * This interface provides methods for checking user registration, retrieving and creating Telegram users,
 * and managing user locale settings.
 * </p>
 */
public interface TelegramUserService {

    /**
     * Checks if a Telegram user with the specified telegram user ID is registered.
     *
     * @param telegramId the unique Telegram user ID
     * @return {@code true} if a user with the given Telegram ID exists, {@code false} otherwise
     */
    boolean isUserRegistered(long telegramId);

    /**
     * Retrieves the Telegram user associated with the specified Telegram user ID.
     *
     * @param telegramId the unique Telegram user ID
     * @return the corresponding {@link TelegramUser} if found; otherwise, an appropriate exception may be thrown or {@code null} returned
     */
    TelegramUser getUserById(long telegramId);

    /**
     * Retrieves the Telegram user associated with the given basic user ID.
     *
     * @param basicUserId the basic user ID that corresponds to a Telegram user
     * @return the corresponding {@link TelegramUser} if found; otherwise, an appropriate exception may be thrown or {@code null} returned
     */
    TelegramUser getByBasicUserId(long basicUserId);

    /**
     * Creates a new Telegram user.
     *
     * @param newUser the {@link TelegramUser} entity representing the new user to be created
     * @return the created {@link TelegramUser} entity
     */
    TelegramUser createTelegramUser(TelegramUser newUser);

    /**
     * Changes the locale setting for the specified Telegram user.
     *
     * @param telegramId the unique Telegram user ID for which the locale is to be changed
     * @param newLocale  the new {@link Locale} to be set for the user
     * @return the updated {@link Locale} of the user
     */
    Locale changeUserLocale(long telegramId, Locale newLocale);

    /**
     * Retrieves the locale setting of the specified Telegram user.
     *
     * @param telegramId the unique Telegram user ID
     * @return the current {@link Locale} of the user
     */
    Locale getUserLocale(long telegramId);
}