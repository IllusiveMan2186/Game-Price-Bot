package com.gpb.common.util;

import com.gpb.common.entity.event.ChangeBasicUserIdEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating Kafka consumer listener container factories for specific event types.
 * <p>
 * This class provides a static method to create a listener factory configured for consuming
 * {@link ChangeBasicUserIdEvent} events from Kafka.
 * </p>
 */
public final class KafkaConsumeFactory {

    private KafkaConsumeFactory() {
    }

    /**
     * Creates a {@link ConcurrentKafkaListenerContainerFactory} for consuming {@link ChangeBasicUserIdEvent} events.
     * <p>
     * The factory is configured with the provided Kafka server address and consumer group ID.
     * </p>
     *
     * @param kafkaServer the address of the Kafka broker.
     * @param groupId     the consumer group ID for the Kafka listener.
     * @return a configured {@link ConcurrentKafkaListenerContainerFactory} for {@link ChangeBasicUserIdEvent}.
     */
    public static ConcurrentKafkaListenerContainerFactory<String, ChangeBasicUserIdEvent> changeIdListener(
            String kafkaServer,
            String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, ChangeBasicUserIdEvent.class.getName());
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        ConcurrentKafkaListenerContainerFactory<String, ChangeBasicUserIdEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        return factory;
    }
}
