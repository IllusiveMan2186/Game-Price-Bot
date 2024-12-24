package com.gpb.telegram.configuration;

import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.service.impl.RestTemplateHandlerServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplateHandlerService restTemplateHandler(RestTemplate restTemplate,
                                                          @Value("${api.key}") String validApiKey,
                                                          @Value("${game.service.url}") String gameServiceUrl) {
        return new RestTemplateHandlerServiceImpl(restTemplate, validApiKey, gameServiceUrl);
    }
}

