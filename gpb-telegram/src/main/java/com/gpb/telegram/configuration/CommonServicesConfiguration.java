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

@Configuration
public class CommonServicesConfiguration {

    @Bean
    public UserLinkerService userLinkerService(RestTemplateHandlerService restTemplateHandler,
                                               KafkaTemplate<String, LinkUsersEvent> linkUsersEventKafkaTemplate) {
        return new UserLinkerServiceImpl(restTemplateHandler, linkUsersEventKafkaTemplate);
    }

    @Bean
    public BasicGameService basicGameService(RestTemplateHandlerService restTemplateHandler,
                                             KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate) {
        return new BasicGameServiceImpl(restTemplateHandler, gameFollowEventKafkaTemplate);
    }
}
