package com.gpb.stores.service.impl;

import com.gpb.stores.bean.EmailEvent;
import com.gpb.stores.bean.user.WebUser;
import com.gpb.stores.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Locale;

import static com.gpb.stores.util.Constants.EMAIL_SERVICE_TOPIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceImplTest {

    KafkaTemplate<Long, EmailEvent> kafkaTemplate = mock(KafkaTemplate.class);

    EmailService emailService = new EmailServiceImpl(kafkaTemplate);

    @Test
    void testSendGameInfoChange_whenSuccessfully_thenShouldSendEmailEvent() {
        String to = "email";
        WebUser user = WebUser.builder()
                .email(to)
                .locale(new Locale("ua")).build();

        emailService.sendGameInfoChange(user, new ArrayList<>());

        verify(kafkaTemplate).send(eq(EMAIL_SERVICE_TOPIC), eq(1L), any(EmailEvent.class));
    }
}