package com.gpb.backend.configuration;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.entity.event.LinkUsersEvent;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.service.UserLinkerService;
import com.gpb.common.service.impl.BasicGameServiceImpl;
import com.gpb.common.service.impl.UserLinkerServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Configuration class for initializing common services used across the application.
 * <p>
 * This configuration creates beans for services that interact with external systems (e.g., REST APIs and Kafka topics).
 * </p>
 */
@Configuration
public class CommonServicesConfiguration {

    /**
     * Creates a {@link UserLinkerService} bean.
     * <p>
     * This service is responsible for linking user accounts.
     * </p>
     *
     * @param restTemplateHandler         the {@link RestTemplateHandlerService} for handling REST calls
     * @param linkUsersEventKafkaTemplate the {@link KafkaTemplate} for sending link user events
     * @return a new instance of {@link UserLinkerServiceImpl}
     */
    @Bean
    public UserLinkerService userLinkerService(RestTemplateHandlerService restTemplateHandler,
                                               KafkaTemplate<String, LinkUsersEvent> linkUsersEventKafkaTemplate) {
        return new UserLinkerServiceImpl(restTemplateHandler, linkUsersEventKafkaTemplate);
    }

    /**
     * Creates a {@link BasicGameService} bean.
     * <p>
     * This service is responsible for game-related operations such as following games.
     * send {@link GameFollowEvent} messages.
     * </p>
     *
     * @param restTemplateHandler         the {@link RestTemplateHandlerService} for handling REST calls
     * @param gameFollowEventKafkaTemplate the {@link KafkaTemplate} for sending game follow events
     * @return a new instance of {@link BasicGameServiceImpl}
     */
    @Bean
    public BasicGameService basicGameService(RestTemplateHandlerService restTemplateHandler,
                                             KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate) {
        return new BasicGameServiceImpl(restTemplateHandler, gameFollowEventKafkaTemplate);
    }
}
