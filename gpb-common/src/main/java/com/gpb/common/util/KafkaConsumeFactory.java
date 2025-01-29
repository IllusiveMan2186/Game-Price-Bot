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
 * A factory class for creating Kafka consumer templates for events.
 */
public class KafkaConsumeFactory {

    private KafkaConsumeFactory() {
    }

    /**
     * Create listener factory of ChangeBasicUserIdEvent
     *
     * @param kafkaServer the Kafka server address
     * @return listener factory of ChangeBasicUserIdEvent
     */
    public static ConcurrentKafkaListenerContainerFactory<String, ChangeBasicUserIdEvent> changeIdListener(
            String kafkaServer,
            String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.gpb.common.entity.event.ChangeBasicUserIdEvent");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        ConcurrentKafkaListenerContainerFactory<String, ChangeBasicUserIdEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        return factory;
    }
}
