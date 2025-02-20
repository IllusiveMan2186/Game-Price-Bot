package com.gpb.telegram.configuration;

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
 * This configuration defines beans for services that interact with external systems,
 * such as REST APIs and Kafka topics. It wires together the necessary dependencies and
 * makes the services available for dependency injection.
 * </p>
 */
@Configuration
public class CommonServicesConfiguration {

    /**
     * Creates a {@link UserLinkerService} bean.
     * <p>
     * The UserLinkerService is responsible for linking user accounts using external
     * communication via REST and Kafka. It utilizes the {@link RestTemplateHandlerService}
     * for executing REST API calls and a {@link KafkaTemplate} for sending {@link LinkUsersEvent} messages.
     * </p>
     *
     * @param restTemplateHandler         the REST template handler used for HTTP calls
     * @param linkUsersEventKafkaTemplate the Kafka template used to send link user events
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
     * The BasicGameService is responsible for handling game-related operations,
     * such as following or unfollowing games. It leverages the {@link RestTemplateHandlerService}
     * for communicating with external APIs and uses a {@link KafkaTemplate} to send {@link GameFollowEvent} messages.
     * </p>
     *
     * @param restTemplateHandler         the REST template handler used for HTTP calls
     * @param gameFollowEventKafkaTemplate the Kafka template used to send game follow events
     * @return a new instance of {@link BasicGameServiceImpl}
     */
    @Bean
    public BasicGameService basicGameService(RestTemplateHandlerService restTemplateHandler,
                                             KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate) {
        return new BasicGameServiceImpl(restTemplateHandler, gameFollowEventKafkaTemplate);
    }
}
