package com.gpb.web.configuration;

import com.gpb.web.bean.game.Game;
import com.gpb.web.util.Constants;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${KAFKA_SERVER_URL}")
    private String kafkaServer;

    @Bean
    public Map < String, Object > consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "gpb");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Use JsonDeserializer for value
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory < String, List<Long>> replyConsumerFactory() {
        return new DefaultKafkaConsumerFactory < > (consumerConfigs(), new StringDeserializer(),
                new JsonDeserializer<>());
    }

    @Bean
    public KafkaMessageListenerContainer < String, List<Long> > replyListenerContainer() {
        ContainerProperties containerProperties = new ContainerProperties(Constants.GAME_SEARCH_RESPONSE_TOPIC);
        return new KafkaMessageListenerContainer< >(replyConsumerFactory(), containerProperties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<Long>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, List<Long>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(replyConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }
}
