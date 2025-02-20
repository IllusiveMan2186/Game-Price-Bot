package com.gpb.telegram.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Configuration class for application localization and internationalization.
 * <p>
 * This configuration creates a {@link MessageSource} bean that loads messages from the specified resource bundle.
 * The message source is configured to use UTF-8 encoding, ensuring that messages with special characters are read correctly.
 * </p>
 */
@Configuration
public class LocalizationConfiguration {

    /**
     * Configures and returns a {@link MessageSource} for resolving localized messages.
     * <p>
     * The {@link ReloadableResourceBundleMessageSource} is set up to load message bundles from the
     * "classpath:/messages/messages" location and uses UTF-8 encoding for reading the message files.
     * </p>
     *
     * @return a configured {@link MessageSource} bean for message resolution
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
