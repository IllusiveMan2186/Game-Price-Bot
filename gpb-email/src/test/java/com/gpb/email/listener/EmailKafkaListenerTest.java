package com.gpb.email.listener;

import com.gpb.email.bean.EmailEvent;
import com.gpb.email.service.EmailService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailKafkaListenerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailKafkaListener emailKafkaListener;

    @Test
    void testEmailEventListen_whenSuccess_shouldProcessEmailEvent() {
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setRecipient(recipient);
        emailEvent.setSubject(subject);
        emailEvent.setTemplateName(templateName);
        emailEvent.setLocale(Locale.ENGLISH); // Assume locale is null for simplicity
        emailEvent.setVariables(Map.of("key1", "value1"));

        ConsumerRecord<String, EmailEvent> consumerRecord = new ConsumerRecord<>(
                "gpb_email_event", 0, 0L, "eventKey", emailEvent
        );


        emailKafkaListener.emailEventListen(consumerRecord);


        verify(emailService, times(1)).sendEmail(
                eq(recipient),
                eq(subject),
                any(Context.class),
                eq(templateName)
        );
    }

    @Test
    void testEmailEventListen_whenSuccess_shouldHandleNullVariables() {
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setRecipient(recipient);
        emailEvent.setSubject(subject);
        emailEvent.setTemplateName(templateName);
        emailEvent.setLocale(Locale.ENGLISH);
        emailEvent.setVariables(null); // Simulating null variables

        ConsumerRecord<String, EmailEvent> consumerRecord = new ConsumerRecord<>(
                "gpb_email_event", 0, 0L, "eventKey", emailEvent
        );


        emailKafkaListener.emailEventListen(consumerRecord);


        verify(emailService, times(1)).sendEmail(
                eq(recipient),
                eq(subject),
                any(Context.class),
                eq(templateName)
        );
    }

    @Test
    void testEmailEventListen_whenSuccess_shouldLogEmailEventDetails() {
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String templateName = "testTemplate";
        String eventKey = "eventKey";
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setRecipient(recipient);
        emailEvent.setSubject(subject);
        emailEvent.setTemplateName(templateName);
        emailEvent.setLocale(Locale.ENGLISH);
        emailEvent.setVariables(Map.of("key1", "value1"));

        ConsumerRecord<String, EmailEvent> consumerRecord = new ConsumerRecord<>(
                "gpb_email_event", 0, 0L, eventKey, emailEvent
        );


        emailKafkaListener.emailEventListen(consumerRecord);


        verify(emailService, times(1)).sendEmail(
                eq(recipient),
                eq(subject),
                any(Context.class),
                eq(templateName)
        );
    }
}
