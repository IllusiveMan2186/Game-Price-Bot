package com.gpb.common.util;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.entity.event.LinkUsersEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating Kafka producer templates for events.
 * <p>
 * This class provides factory methods to create {@link KafkaTemplate} instances for specific event types,
 * such as {@link GameFollowEvent} and {@link LinkUsersEvent}. Each template is configured with a JSON serializer
 * for the message value and a string serializer for the message key.
 * </p>
 */
public final class KafkaProducerFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private KafkaProducerFactory() {
    }

    /**
     * Creates a {@link KafkaTemplate} for producing {@link GameFollowEvent} messages.
     *
     * @param kafkaServer the Kafka server address (e.g., "localhost:9092").
     * @return a configured {@link KafkaTemplate} for sending {@link GameFollowEvent} messages.
     */
    public static KafkaTemplate<String, GameFollowEvent> createGameFollowEventTemplate(final String kafkaServer) {
        final Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }

    /**
     * Creates a {@link KafkaTemplate} for producing {@link LinkUsersEvent} messages.
     *
     * @param kafkaServer the Kafka server address (e.g., "localhost:9092").
     * @return a configured {@link KafkaTemplate} for sending {@link LinkUsersEvent} messages.
     */
    public static KafkaTemplate<String, LinkUsersEvent> createLinkUsersEventTemplate(final String kafkaServer) {
        final Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }
}
