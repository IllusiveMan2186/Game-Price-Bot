package com.gpb.backend.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Configuration class for application localization and internationalization.
 * <p>
 * This configuration defines a {@link MessageSource} bean that loads messages from the specified resource bundles.
 * </p>
 */
@Configuration
public class LocalizationConfiguration {

    /**
     * Configures the message source for resolving internationalized messages.
     *
     * @return the configured {@link MessageSource} bean for message resolution.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setBasenames("classpath:/messages/messages");
        return messageSource;
    }
}
