package com.gpb.web.configuration;

import com.gpb.web.bean.EmailEvent;
import com.gpb.web.util.Constants;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${KAFKA_SERVER_URL}")
    private String kafkaServer;

    @Bean
    public ProducerFactory<Long, EmailEvent> producerEmailEventFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public ProducerFactory<String, Long> producerFollowFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<Long, EmailEvent> kafkaEmailEventTemplate() {
        return new KafkaTemplate<>(producerEmailEventFactory());
    }

    @Bean
    public ProducerFactory<String, String> producerResponseFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, List<Long>> kafkaResponseTemplate(
            KafkaMessageListenerContainer<String, List<Long>> consumerFactory) {
        ReplyingKafkaTemplate<String, String, List<Long>> template =
                new ReplyingKafkaTemplate<>(producerResponseFactory(), consumerFactory);
        template.setDefaultReplyTimeout(Duration.ofSeconds(Constants.SEARCH_REQUEST_WAITING_TIME));
        template.setBinaryCorrelation(false);
        return template;
    }

    @Bean
    public KafkaTemplate<String, Long> kafkaFollowTemplate() {
        return new KafkaTemplate<>(producerFollowFactory());
    }
}
