package com.gpb.web.service.impl;

import com.gpb.web.bean.EmailEvent;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.Locale;

import static com.gpb.web.util.Constants.EMAIL_SERVICE_TOPIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceImplTest {

    KafkaTemplate<Long, EmailEvent> kafkaTemplate = mock(KafkaTemplate.class);

    EmailService emailService = new EmailServiceImpl(kafkaTemplate);

    @Test
    public void sendGameInfoChangeSuccessfullyShouldSendEmailEvent() {
        String to = "email";
        WebUser user = WebUser.builder()
                .email(to)
                .locale(new Locale("ua")).build();

        emailService.sendGameInfoChange(user, new ArrayList<>());

        verify(kafkaTemplate).send(eq(EMAIL_SERVICE_TOPIC), eq(1L), any(EmailEvent.class));
    }
}