package com.gpb.telegram.configuration;

import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.service.impl.RestTemplateHandlerServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for setting up REST communication services.
 * <p>
 * This configuration creates a {@link RestTemplate} bean for executing HTTP requests and
 * a {@link RestTemplateHandlerService} bean that wraps the RestTemplate with additional functionality.
 * </p>
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Creates a {@link RestTemplateHandlerService} bean which wraps the provided {@link RestTemplate}
     * for making REST calls to external services.
     * <p>
     * The bean is configured using the provided API key and game service URL from the application properties.
     * </p>
     *
     * @param restTemplate  the {@link RestTemplate} used for executing HTTP requests
     * @param validApiKey   the API key for authenticating external requests (injected from configuration)
     * @param gameServiceUrl the URL of the game service to which requests are directed (injected from configuration)
     * @return an instance of {@link RestTemplateHandlerServiceImpl} configured with the given dependencies
     */
    @Bean
    public RestTemplateHandlerService restTemplateHandler(RestTemplate restTemplate,
                                                          @Value("${api.key}") String validApiKey,
                                                          @Value("${game.service.url}") String gameServiceUrl) {
        return new RestTemplateHandlerServiceImpl(restTemplate, validApiKey, gameServiceUrl);
    }
}