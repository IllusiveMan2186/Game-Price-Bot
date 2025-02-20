package com.gpb.backend.configuration.kafka;

import com.gpb.common.listener.ChangeBasicUserIdListener;
import com.gpb.common.service.ChangeUserBasicIdService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaListenerConfig {

    @Bean
    public ChangeBasicUserIdListener changeBasicUserIdListener(ChangeUserBasicIdService changeIdService) {
        return new ChangeBasicUserIdListener(changeIdService);
    }
}
