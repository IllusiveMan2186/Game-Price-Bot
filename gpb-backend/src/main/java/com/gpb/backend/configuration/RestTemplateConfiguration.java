package com.gpb.backend.configuration;

import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.service.impl.RestTemplateHandlerServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for creating and wiring beans related to REST communication.
 */
@Configuration
public class RestTemplateConfiguration {

    /**
     * Creates a {@link RestTemplate} bean used for executing HTTP requests.
     *
     * @return a new instance of {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Creates a {@link RestTemplateHandlerService} bean which wraps the {@link RestTemplate}
     * for making synchronized REST calls to other services.
     *
     * @param restTemplate the {@link RestTemplate} used for HTTP requests
     * @param validApiKey  the API key used for authenticating requests to the target service
     * @param gameServiceUrl the URL of the game service to which requests are directed
     * @return an instance of {@link RestTemplateHandlerServiceImpl}
     */
    @Bean
    public RestTemplateHandlerService restTemplateHandler(RestTemplate restTemplate,
                                                          @Value("${api.key}") String validApiKey,
                                                          @Value("${game.service.url}") String gameServiceUrl) {
        return new RestTemplateHandlerServiceImpl(restTemplate, validApiKey, gameServiceUrl);
    }
}