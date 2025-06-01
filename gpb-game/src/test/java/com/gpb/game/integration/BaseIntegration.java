package com.gpb.game.integration;

import com.gpb.common.entity.event.NotificationEvent;
import com.gpb.game.listener.GameRequestListener;
import com.gpb.game.listener.UserRequestListener;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

public class BaseIntegration {

    protected static final String API_KEY = "api key";

    @MockBean
    protected KafkaTemplate<String, NotificationEvent> responseKafkaTemplate;
    @MockBean
    protected GameRequestListener gameRequestListener;
    @MockBean
    protected UserRequestListener userRequestListener;


    @BeforeAll
    protected static void beforeAll() {
        System.setProperty("IMAGE_FOLDER", "");
        System.setProperty("KAFKA_SERVER_URL", "");
        System.setProperty("API_KEY", API_KEY);
    }
}
