package com.gpb.telegram.configuration;

import com.gpb.telegram.bot.GamePriceBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Initializes and registers the Telegram bot upon application startup.
 * <p>
 * This component listens for the {@link ContextRefreshedEvent} and registers the {@link GamePriceBot}
 * with the Telegram Bots API.
 * </p>
 */
@Slf4j
@Component
public class BotInitializer {

    private final GamePriceBot gamePriceBot;

    /**
     * Constructs a new BotInitializer with the specified GamePriceBot.
     *
     * @param gamePriceBot the Telegram bot to be registered
     */
    public BotInitializer(final GamePriceBot gamePriceBot) {
        this.gamePriceBot = gamePriceBot;
    }

    /**
     * Registers the Telegram bot when the application context is refreshed.
     *
     * @param event the context refreshed event
     * @throws TelegramApiException if an error occurs during bot registration
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init(final ContextRefreshedEvent event) throws TelegramApiException {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(gamePriceBot);
            log.info("Successfully registered the Telegram bot: {}", gamePriceBot.getBotUsername());
        } catch (TelegramApiException e) {
            log.error("Failed to register the Telegram bot: {}", gamePriceBot.getBotUsername(), e);
            // Wrap and rethrow as a RuntimeException if you want the application to fail fast.
            throw new RuntimeException("Failed to register Telegram bot", e);
        }
    }
}