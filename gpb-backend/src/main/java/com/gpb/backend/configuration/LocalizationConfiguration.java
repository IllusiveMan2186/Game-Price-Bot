package com.gpb.backend.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class LocalizationConfiguration {

    private static final String MESSAGES_BASENAME_PATH = "/src/main/resources/messages/messages";

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setBasenames("file:" + System.getProperty("user.dir") + MESSAGES_BASENAME_PATH);
        return messageSource;
    }
}
