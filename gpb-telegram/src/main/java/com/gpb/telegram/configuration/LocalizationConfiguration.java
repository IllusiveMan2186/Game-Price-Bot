package com.gpb.telegram.configuration;

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
        messageSource.setBasenames("file:" + System.getProperty("user.dir") + MESSAGES_BASENAME_PATH);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
