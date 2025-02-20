package com.gpb.telegram.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Telegram bot.
 * <p>
 * This class binds to properties prefixed with "bot" in the application configuration file (e.g., application.yml).
 * It contains the bot's username and authentication token.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "bot")
@Data
public class BotConfiguration {

    /**
     * The bot's username.
     */
    private String name;

    /**
     * The authentication token for the bot.
     */
    private String token;
}