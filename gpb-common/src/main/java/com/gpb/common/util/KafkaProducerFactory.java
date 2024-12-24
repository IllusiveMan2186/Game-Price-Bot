package com.gpb.common.util;

import com.gpb.common.entity.event.GameFollowEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory class for creating Kafka producer templates for GameFollowEvent.
 */
public class KafkaProducerFactory {

    private KafkaProducerFactory() {
    }

    /**
     * Creates a KafkaTemplate for producing GameFollowEvent messages.
     *
     * @param kafkaServer the Kafka server address
     * @return a configured KafkaTemplate instance
     */
    public static KafkaTemplate<String, GameFollowEvent> createGameFollowEventTemplate(String kafkaServer) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }
}
